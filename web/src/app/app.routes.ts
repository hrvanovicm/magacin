import { Routes } from '@angular/router';
import { ScaffoldLayout } from './shared/scaffold.layout';
import { ArticleIndexPage } from './features/articles/index.page';

export const routes: Routes = [
    {
        path: '',
        component: ScaffoldLayout,
        children: [
            { path: ``, component: ArticleIndexPage }
        ]
    }
];
