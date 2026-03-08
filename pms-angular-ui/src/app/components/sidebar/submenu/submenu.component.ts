import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { MenuItem } from '../menu-item.model';

@Component({
  selector: 'app-submenu',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './submenu.component.html',
  styleUrls: ['./submenu.component.css']
})
export class SubmenuComponent {
  @Input() item!: MenuItem;
  @Input() collapsed: boolean = false;
  @Input() level: number = 0;
  @Output() expandedChange = new EventEmitter<boolean>();
  
  constructor(private router: Router) {}
  
  toggleExpand() {
    if (!this.collapsed) {
      this.item.expanded = !this.item.expanded;
      this.expandedChange.emit(this.item.expanded);
    }
  }

  onChildExpandedChange(expanded: boolean, expandedChild: MenuItem) {
    if (expanded) {
      this.expandedChange.emit(true);
    }
  }

  onChildClick() {
    this.expandedChange.emit(false);
  }
  
  isActive(): boolean {
    if (this.item.route && this.router.url === this.item.route) {
      return true;
    }
    return false;
  }

  private isRouteActive(menuItem: MenuItem): boolean {
    if (menuItem.route && this.router.url === menuItem.route) {
      return true;
    }
    if (menuItem.children) {
      return menuItem.children.some(child => this.isRouteActive(child));
    }
    return false;
  }

  hasActiveChild(): boolean {
    if (this.item.children) {
      return this.item.children.some(child => this.isRouteActive(child));
    }
    return false;
  }

  isChildActive(child: MenuItem): boolean {
    return this.router.url === child.route;
  }
}

