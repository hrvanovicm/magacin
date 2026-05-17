import { Component } from "@angular/core";
import { MatToolbar } from "@angular/material/toolbar";
import { RouterOutlet } from "@angular/router";
import { MatDivider } from "@angular/material/divider";

@Component({
  template: `
    <mat-toolbar class="shrink-0">
      <span class="flex-1 basis-auto"></span>
      <span>Magacin</span>
      <span class="flex-1 basis-auto"></span>
    </mat-toolbar>
    <main class="flex flex-1 min-h-0 w-full overflow-hidden flex-col">
      <mat-divider></mat-divider>
      <router-outlet></router-outlet>
    </main>
  `,
  imports: [MatToolbar, MatDivider, RouterOutlet],
  styles: `
    :host {
      @apply flex flex-col h-full w-full overflow-hidden;
    }
  `
})
export class GuestLayout {

}
