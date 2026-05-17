import { Component, computed, input } from '@angular/core';
import { Article } from '../../api';

type MonthBar = {
  label: string;
  key: string;
  in: number;
  out: number;
  gx: number;
  bw: number;
  gw: number;
  inH: number;
  outH: number;
};

@Component({
  selector: 'app-article-analytics',
  imports: [],
  template: `
    <div class="w-full">
      <svg [attr.viewBox]="'0 0 560 240'" class="w-full" style="max-height:240px">
        <line x1="50" y1="10" x2="50" y2="200" stroke="#d1d5db" stroke-width="1"/>
        <line x1="50" y1="200" x2="550" y2="200" stroke="#d1d5db" stroke-width="1"/>

        @for (tick of ticks(); track tick.label) {
          <line x1="47" [attr.y1]="tick.y" x2="550" [attr.y2]="tick.y"
                stroke="#e5e7eb" stroke-width="1" stroke-dasharray="4 3"/>
          <text x="44" [attr.y]="tick.y + 4" text-anchor="end" font-size="9" fill="#9ca3af">{{ tick.label }}</text>
        }

        @for (bar of bars(); track bar.key) {
          @if (bar.inH > 0) {
            <rect [attr.x]="bar.gx + bar.gw * 0.08" [attr.y]="200 - bar.inH"
                  [attr.width]="bar.bw" [attr.height]="bar.inH" fill="#22c55e" rx="2"/>
          }
          @if (bar.outH > 0) {
            <rect [attr.x]="bar.gx + bar.gw * 0.53" [attr.y]="200 - bar.outH"
                  [attr.width]="bar.bw" [attr.height]="bar.outH" fill="#ef4444" rx="2"/>
          }
          <text [attr.x]="bar.gx + bar.gw / 2" y="213" text-anchor="middle" font-size="8.5" fill="#6b7280">{{ bar.label }}</text>
        }

        <rect x="55" y="223" width="10" height="10" fill="#22c55e" rx="2"/>
        <text x="69" y="232" font-size="9" fill="#374151">Ulaz (primke / radni nalozi)</text>
        <rect x="200" y="223" width="10" height="10" fill="#ef4444" rx="2"/>
        <text x="214" y="232" font-size="9" fill="#374151">Izlaz (otpremnice / radni nalozi)</text>
      </svg>
    </div>
  `,
})
export class ArticleAnalyticsComponent {
  readonly article = input.required<Article>();
  readonly data = input.required<{ label: string; key: string; in: number; out: number }[]>();

  private readonly max = computed(() => Math.max(1, ...this.data().flatMap(m => [m.in, m.out])));

  readonly ticks = computed(() => {
    const m = this.max();
    return [
      { label: '0', y: 200 },
      { label: String(+(m / 2).toFixed(1)), y: 105 },
      { label: String(+m.toFixed(1)), y: 10 },
    ];
  });

  readonly bars = computed((): MonthBar[] => {
    const m = this.max();
    const gw = 500 / 12;
    const bw = gw * 0.35;
    return this.data().map((d, i) => ({
      ...d,
      gx: 50 + i * gw,
      gw,
      bw,
      inH: (d.in / m) * 190,
      outH: (d.out / m) * 190,
    }));
  });
}
