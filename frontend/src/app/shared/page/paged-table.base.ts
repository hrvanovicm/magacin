import {Directive, EventEmitter, Output} from '@angular/core';
import {Sort} from '@angular/material/sort';

@Directive()
export abstract class PagedTableBase {
  @Output() sorted = new EventEmitter<Sort>();
  abstract setData(data: any[]): void;
}
