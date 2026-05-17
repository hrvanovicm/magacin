import { Component, inject, input } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { Location } from '@angular/common';
import { MatIconButton } from '@angular/material/button';

@Component({
  imports: [MatToolbarModule, MatIconModule, MatIconButton],
  selector: 'app-back-toolbar',
  template: `
    <mat-toolbar>
      <button matIconButton (click)="location.back()">
        <mat-icon>arrow_back</mat-icon>
      </button>
      <span class="ml-2 text-xl">{{ title() }}</span>
      <span class="flex-1"></span>
      <ng-content select="[actions]" />
    </mat-toolbar>
  `,
})
export class BackToolbarComponent {
  readonly location = inject(Location);
  readonly title = input.required<string>();
}
