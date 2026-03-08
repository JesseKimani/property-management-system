import { Component, Input, Output, EventEmitter, HostListener, PLATFORM_ID, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { MenuItem } from './menu-item.model';
import { SubmenuComponent } from './submenu/submenu.component';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterModule, SubmenuComponent],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})

export class SidebarComponent {
  @Input() menuItems: MenuItem[] = [];
  @Input() title: string = 'Navigation';
  @Input() collapsed: boolean = false;
  @Output() collapsedChange = new EventEmitter<boolean>();
  
  mobileOpen: boolean = false;
  screenWidth: number = 0;
  
  constructor(@Inject(PLATFORM_ID) private platformId: Object) {
    // Check if we're in the browser before accessing window
    if (isPlatformBrowser(this.platformId)) {
      this.screenWidth = window.innerWidth;
    }
  }
  
  @HostListener('window:resize', ['$event'])
  onResize() {
    if (isPlatformBrowser(this.platformId)) {
      this.screenWidth = window.innerWidth;
      if (this.screenWidth > 768 && this.mobileOpen) {
        this.mobileOpen = false;
      }
    }
  }
  
  toggleCollapse() {
    this.collapsed = !this.collapsed;
    this.collapsedChange.emit(this.collapsed);
  }
  
  toggleMobileMenu() {
    this.mobileOpen = !this.mobileOpen;
  }
  
  closeMobileMenu() {
    this.mobileOpen = false;
  }

  onSubmenuExpandedChange(expanded: boolean, expandedItem: MenuItem) {
    if (expanded) {
      // Close all other expanded items
      this.closeAllSubmenusExcept(expandedItem, this.menuItems);
    }
  }

  onMenuItemClick() {
    // Close all submenus when a regular menu item is clicked
    this.closeAllSubmenus(this.menuItems);
  }

  private closeAllSubmenusExcept(exceptItem: MenuItem, items: MenuItem[]) {
    items.forEach(item => {
      if (item !== exceptItem && item.expanded) {
        item.expanded = false;
      }
      if (item.children) {
        this.closeAllSubmenusExcept(exceptItem, item.children);
      }
    });
  }

  private closeAllSubmenus(items: MenuItem[]) {
    items.forEach(item => {
      if (item.expanded) {
        item.expanded = false;
      }
      if (item.children) {
        this.closeAllSubmenus(item.children);
      }
    });
  }

  isMenuItemActive(item: MenuItem): boolean {
    if (isPlatformBrowser(this.platformId)) {
      return window.location.pathname === item.route;
    }
    return false;
  }
}

