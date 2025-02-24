import { Component, inject, signal } from "@angular/core";
import { Table } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { IconFieldModule } from 'primeng/iconfield';
import { InputIconModule } from 'primeng/inputicon';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { InputTextModule } from 'primeng/inputtext';
import { MultiSelectModule } from 'primeng/multiselect';
import { SelectModule } from 'primeng/select';
import { CommonModule } from '@angular/common';
import { TableModule } from 'primeng/table';
import { FormsModule } from "@angular/forms";
import {Slider} from 'primeng/slider';
import {ArticleService} from './services/article.service';

@Component({
  imports: [TableModule, TagModule, IconFieldModule, InputTextModule, InputIconModule, MultiSelectModule, SelectModule, HttpClientModule, CommonModule, FormsModule],
    template: `
    <div class="card h-full w-full">
        <p-table
            class="h-full w-full"
            #dt2
            [value]="customers"
            dataKey="id"
            [loading]="loading"
            [scrollable]="true" scrollHeight="800px"
            [virtualScroll]="true"
            [virtualScrollItemSize]="46"
            [tableStyle]="{ 'width': '100%', 'display': 'table' }"
        >
            <ng-template #header>
                <tr>
                    <th>Rb</th>
                    <th pSortableColumn="name">
                        Naziv <p-sortIcon field="name" class="pl-3" />
                    </th>
                    <th pSortableColumn="code">
                      Šifra <p-sortIcon field="code" class="pl-3" />
                    </th>
                    <th pSortableColumn="name">
                      Kategorija <p-sortIcon field="category.name" class="pl-3" />
                    </th>
                    <th>Oznake</th>
                    <th pSortableColumn="inStockAmount">
                      Na stanju <p-sortIcon field="inStockAmount" class="pl-3" />
                    </th>
                </tr>
                <tr>
                    <th></th>
                    <th>
                        <p-columnFilter field="name" matchMode="contains" [showMenu]="false">
                            <ng-template pTemplate="filter" let-value let-filter="filterCallback">
                                <input type="text"
                                pInputText
                                [ngModel]="value"
                                (ngModelChange)="filter($event)"
                                class="p-inputtext"
                                placeholder="Unesite naziv">
                            </ng-template>
                        </p-columnFilter>
                    </th>
                    <th>
                        <p-columnFilter field="code" matchMode="contains" [showMenu]="false">
                            <ng-template pTemplate="filter" let-value let-filter="filterCallback">
                                <input type="text"
                                pInputText
                                [ngModel]="value"
                                (ngModelChange)="filter($event)"
                                class="p-inputtext"
                                placeholder="Unesite šifru">
                            </ng-template>
                        </p-columnFilter>
                    </th>
                    <th>
                        <p-columnFilter field="category.name" matchMode="in" [showMenu]="false" [showMatchModes]="false" [showOperator]="false" [showAddButton]="false">
                            <ng-template #filter pTemplate="filter" let-value let-filter="filterCallback">
                                <p-multiSelect optionValue="name" optionLabel="name" [ngModel]="value" class="w-[220px]" [options]="representatives"
                                               placeholder="Odaberite kategoriju" (onChange)="filter($event.value)">
                                    <ng-template let-option #item>
                                        <div class="inline-block align-middle">
                                            <span class="ml-1 mt-1">{{ option.name }}</span>
                                        </div>
                                    </ng-template>
                                </p-multiSelect>
                            </ng-template>
                        </p-columnFilter>
                    </th>
                    <th>
                      <p-columnFilter field="category.name" matchMode="in" [showMenu]="false" [showMatchModes]="false" [showOperator]="false" [showAddButton]="false">
                        <ng-template #filter pTemplate="filter" let-value let-filter="filterCallback">
                          <p-multiSelect optionValue="name" optionLabel="name" [ngModel]="value" class="w-[220px]" [options]="representatives"
                                         placeholder="Odaberite oznake" (onChange)="filter($event.value)">
                            <ng-template let-option #item>
                              <div class="inline-block align-middle">
                                <span class="ml-1 mt-1">{{ option.name }}</span>
                              </div>
                            </ng-template>
                          </p-multiSelect>
                        </ng-template>
                      </p-columnFilter>
                    </th>
                    <th>
                      <div class="flex flex-row gap-x-2 items-center">
                        <label class="font-normal">Nema na stanju</label>
                        <p-columnFilter type="boolean" field="lowInStockAmount"></p-columnFilter>
                      </div>
                    </th>
                </tr>
            </ng-template>
            <ng-template #body let-article let-i="rowIndex">
                <tr class="w-full">
                    <td>{{ i + 1 }}.</td>
                    <td>{{ article.name }}</td>
                    <td>{{ article.code }}</td>
                    <td>{{ article.category.name }}</td>
                    <td>
                      <div class="flex flex-row flex-wrap gap-x-1">
                        @for (tag of article.tags; track tag) {
                          <p-tag severity="secondary" [value]="tag"></p-tag>
                        }
                      </div>
                    </td>
                    <td><strong>{{ article.inStockAmount }}</strong> ({{ article.unitMeasure.shortName}})</td>
                </tr>
            </ng-template>
            <ng-template #emptymessage>
                <tr>
                    <td colspan="5">Nijedan proizvod nije pronađen!</td>
                </tr>
            </ng-template>
        </p-table>
        </div>

    `
})
export class ArticleIndexPage {
    articleService = inject(ArticleService);
    nameFilter = signal("test")
    customers: any = [];

    representatives = [{name: "Electronics"}, {name: "Clothing"}, {name: "Toys"}];

    statuses!: any[];

    loading: boolean = false;

    activityValues: number[] = [0, 100]

    clear(table: Table) {
        table.clear();
    }

    mapCategories(v: any) {
        return v.map((item: any) => item.name).filter((item: any) => item);
    }

    ngOnInit() {
        this.articleService.getArticles().then((response: any) => {
          this.customers = response;
        })
    }
}
