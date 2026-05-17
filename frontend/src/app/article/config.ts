import {Routes} from '@angular/router';
import {ProductIndexPage} from './index.page';
import {EditPage} from './edit.page';
import {UnsavedChangesGuard} from '../core/guards';

export const ARTICLE_LINKS = {
  index: () => `/articles`,
  create: () => `/articles/new`,
  edit: (id: number) => `/articles/${id}`,
}

export const ARTICLE_ROUTES: Routes = [
  {
    path: '',
    children: [
      {path: '', component: ProductIndexPage},
      {path: 'new', component: EditPage, canDeactivate: [UnsavedChangesGuard]},
      {path: ':id', component: EditPage, canDeactivate: [UnsavedChangesGuard]},
    ]
  }
];
