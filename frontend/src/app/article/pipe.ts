import {Pipe, PipeTransform} from '@angular/core';
import {ArticleCategory} from '../api';

type UnitMeasure = any

@Pipe({name: 'ReadableArticleCategoryPipe'})
export class ReadableArticleCategoryPipePipe implements PipeTransform {
  transform(value: string): any {
    switch (value) {
      case ArticleCategory.PRODUCT:
        return 'Proizvod';
      case ArticleCategory.COMMERCIAL:
        return 'Komercijala';
      case ArticleCategory.RAW_MATERIAL:
        return 'Sirovina';
      default:
        throw new Error('Unknown category');
    }
  }
}

@Pipe({name: 'ReadableArticleInStockAmountPipe'})
export class ReadableArticleInStockAmountPipePipe implements PipeTransform {
  transform(value: number, unitMeasure?: UnitMeasure): any {
    if (value === 0) {
      return 'Nema na stanju';
    }

    if (!unitMeasure) {
      return value.toFixed(2);
    }

    return `${value.toFixed(2)} (${unitMeasure!.name ?? ''})`;
  }
}
