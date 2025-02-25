import { ApplicationConfig, LOCALE_ID, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient } from '@angular/common/http';

import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { providePrimeNG } from 'primeng/config';
import Aura from '@primeng/themes/aura';
import { provideTranslateService } from '@ngx-translate/core';
import {ArticleService} from './features/articles/services/article.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(),
    provideAnimationsAsync(),
    ArticleService,
  providePrimeNG({
    theme: {
      preset: Aura,
      options: {
        darkModeSelector: '.dark',
        cssLayer: {
          name: 'primeng',
          order: 'tailwind-base, primeng, tailwind-utilities',
        },
      },
    },
    translation: {
      "startsWith": "Počinje sa",
      "contains": "Sadrži",
      "notContains": "Ne sadrži",
      "endsWith": "Završava sa",
      "equals": "Jednako",
      "notEquals": "Nije jednako",
      "noFilter": "Bez filtra",
      "lt": "Manje od",
      "lte": "Manje ili jednako",
      "gt": "Veće od",
      "gte": "Veće ili jednako",
      "is": "Je",
      "isNot": "Nije",
      "before": "Prije",
      "after": "Poslije",
      "dateIs": "Datum je",
      "dateIsNot": "Datum nije",
      "dateBefore": "Datum prije",
      "dateAfter": "Datum poslije",
      "clear": "Očisti",
      "apply": "Primijeni",
      "matchAll": "Podudara se sa svim",
      "matchAny": "Podudara se sa bilo čim",
      "addRule": "Dodaj pravilo",
      "removeRule": "Ukloni pravilo",
      "accept": "Prihvati",
      "reject": "Odbaci",
      "choose": "Izaberi",
      "upload": "Otpremi",
      "cancel": "Otkaži",
      "fileSizeTypes": ["B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB"],
      "dayNames": ["Nedjelja", "Ponedjeljak", "Utorak", "Srijeda", "Četvrtak", "Petak", "Subota"],
      "dayNamesShort": ["Ned", "Pon", "Uto", "Sri", "Čet", "Pet", "Sub"],
      "dayNamesMin": ["Ne", "Po", "Ut", "Sr", "Če", "Pe", "Su"],
      "monthNames": ["Januar", "Februar", "Mart", "April", "Maj", "Juni", "Juli", "August", "Septembar", "Oktobar", "Novembar", "Decembar"],
      "monthNamesShort": ["Jan", "Feb", "Mar", "Apr", "Maj", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dec"],
      "dateFormat": "Format datuma",
      "today": "Danas",
      "weekHeader": "Zaglavlje sedmice",
      "weak": "Slaba",
      "medium": "Srednja",
      "strong": "Snažna",
      "passwordPrompt": "Upit za lozinku",
      "emptyMessage": "Prazna poruka",
      "emptyFilterMessage": "Prazna poruka filtra",
      "fileChosenMessage": "Izabrani fajl",
      "noFileChosenMessage": "Nema izabranog fajla",
      "pending": "U toku",
      "chooseYear": "Izaberi godinu",
      "chooseMonth": "Izaberi mjesec",
      "chooseDate": "Izaberi datum",
      "prevDecade": "Prethodna decenija",
      "nextDecade": "Sljedeća decenija",
      "prevYear": "Prethodna godina",
      "nextYear": "Sljedeća godina",
      "prevMonth": "Prethodni mjesec",
      "nextMonth": "Sljedeći mjesec",
      "prevHour": "Prethodni sat",
      "nextHour": "Sljedeći sat",
      "prevMinute": "Prethodna minuta",
      "nextMinute": "Sljedeća minuta",
      "prevSecond": "Prethodna sekunda",
      "nextSecond": "Sljedeća sekunda",
      "am": "Prijepodne",
      "pm": "Poslijepodne",
      "searchMessage": "Poruka pretrage",
      "selectionMessage": "Poruka izbora",
      "emptySelectionMessage": "Prazna poruka izbora",
      "emptySearchMessage": "Prazna poruka pretrage",
      "aria": {
        "trueLabel": "Tačno",
        "falseLabel": "Netačno",
        "nullLabel": "Nije selektovano",
        "star": "1 zvijezda",
        "stars": "{star} zvjezdice",
        "selectAll": "Sve stavke selektovane",
        "unselectAll": "Sve stavke deselectovane",
        "close": "Zatvori",
        "previous": "Prethodni",
        "next": "Sljedeći",
        "navigation": "Navigacija",
        "scrollTop": "Pomjeri se na vrh",
        "moveTop": "Pomjeri se na vrh",
        "moveUp": "Pomjeri se gore",
        "moveDown": "Pomjeri se dolje",
        "moveBottom": "Pomjeri se na dno",
        "moveToTarget": "Pomjeri na ciljani element",
        "moveToSource": "Pomjeri na izvorni element",
        "moveAllToTarget": "Pomjeri sve na ciljani element",
        "moveAllToSource": "Pomjeri sve na izvorni element",
        "pageLabel": "{page}",
        "firstPageLabel": "Prva stranica",
        "lastPageLabel": "Posljednja stranica",
        "nextPageLabel": "Sljedeća stranica",
        "prevPageLabel": "Prethodna stranica",
        "rowsPerPageLabel": "Redova po stranici",
        "previousPageLabel": "Prethodna stranica",
        "jumpToPageDropdownLabel": "Skoči na stranicu",
        "jumpToPageInputLabel": "Unesite broj stranice",
        "selectRow": "Red je selektovan",
        "unselectRow": "Red nije selektovan",
        "expandRow": "Proširi red",
        "collapseRow": "Smanji red",
        "showFilterMenu": "Prikaži meni filtera",
        "hideFilterMenu": "Sakrij meni filtera",
        "filterOperator": "Operator filtera",
        "filterConstraint": "Kriterijum filtera",
        "editRow": "Uredi red",
        "saveEdit": "Spremi izmjene",
        "cancelEdit": "Otkaži izmjene",
        "listView": "Prikaz liste",
        "gridView": "Prikaz mreže",
        "slide": "Klizni",
        "slideNumber": "{slideNumber}",
        "zoomImage": "Povećaj sliku",
        "zoomIn": "Povećaj",
        "zoomOut": "Smanji",
        "rotateRight": "Rotiraj udesno",
        "rotateLeft": "Rotiraj ulijevo",
        "listLabel": "Oznaka liste",
        "selectColor": "Izaberite boju",
        "removeLabel": "Ukloni oznaku",
        "browseFiles": "Pregledaj datoteke",
        "maximizeLabel": "Maksimiziraj"
      }
    }

  }), [{ provide: LOCALE_ID, useValue: 'bs-Latn' }], provideTranslateService()
]
};
