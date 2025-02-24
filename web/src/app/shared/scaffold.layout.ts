import {Component} from "@angular/core";
import {RouterOutlet} from "@angular/router";
import {ToolbarModule} from 'primeng/toolbar';

import {Drawer} from 'primeng/drawer';
import {ButtonModule} from 'primeng/button';
import {Ripple} from 'primeng/ripple';
import {DrawerModule} from 'primeng/drawer';

@Component({
  imports: [RouterOutlet, ToolbarModule, ButtonModule, Ripple, DrawerModule],
  template: `
    <p-toolbar>
      <ng-template #start>
        <h4 class="text-2xl">Magacin</h4>
      </ng-template>
    </p-toolbar>

    <div class="flex flex-row h-full w-full">
      <aside class="w-[300px]">
        <ul class="list-none p-4 m-0">
          <li>
            <ul class="list-none p-0 m-0 overflow-hidden">
              <li>
                <a pRipple
                   class="flex items-center cursor-pointer p-4 rounded-border text-surface-700 dark:text-surface-100 hover:bg-surface-100 dark:hover:bg-surface-700 duration-150 transition-colors p-ripple">
                  <i class="pi pi-folder mr-2"></i>
                  <span class="font-medium">Roba</span>
                </a>
              </li>
              <li>
                <a pRipple
                   class="flex items-center cursor-pointer p-4 rounded-border text-surface-700 dark:text-surface-100 hover:bg-surface-100 dark:hover:bg-surface-700 duration-150 transition-colors p-ripple">
                  <i class="pi pi-chart-bar mr-2"></i>
                  <span class="font-medium">Primke</span>
                </a>
              </li>
              <li>
                <a pRipple
                   class="flex items-center cursor-pointer p-4 rounded-border text-surface-700 dark:text-surface-100 hover:bg-surface-100 dark:hover:bg-surface-700 duration-150 transition-colors p-ripple">
                  <i class="pi pi-cog mr-2"></i>
                  <span class="font-medium">Optremnice</span>
                </a>
              </li>
              <li>
                <a pRipple
                   class="flex items-center cursor-pointer p-4 rounded-border text-surface-700 dark:text-surface-100 hover:bg-surface-100 dark:hover:bg-surface-700 duration-150 transition-colors p-ripple">
                  <i class="pi pi-cog mr-2"></i>
                  <span class="font-medium">Postavke</span>
                </a>
              </li>
            </ul>
          </li>
        </ul>
      </aside>
      <main class="w-full h-full">
        <router-outlet/>
      </main>
    </div>
  `
})
export class ScaffoldLayout {

}
