import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {environment} from '../../../../environments/environment';
import {lastValueFrom, Observable} from 'rxjs';

@Injectable()
export class ArticleService {
  private _http: HttpClient = inject(HttpClient);

  async getArticles(): Promise<any[]> {
    let response = await lastValueFrom(this._http.get<any>(`${environment.API_URL}/articles`));
    return response.content;
  }

  async getCategories(): Promise<any[]> {
    let response = await lastValueFrom(this._http.get<any>(`${environment.API_URL}/articles`));
    return response.content;
  }
}
