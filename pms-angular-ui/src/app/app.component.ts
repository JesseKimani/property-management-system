import { Component, ViewChild, OnInit } from '@angular/core';
import { NavigationEnd, RouterOutlet } from '@angular/router';
import { Router } from '@angular/router';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { MenuItem } from './components/sidebar/menu-item.model';
import { CommonModule } from '@angular/common';
import { AuthService } from './services/auth.service';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, SidebarComponent, CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit {
  title = 'pms-ui';
  isLoginRoute: boolean = false;

  @ViewChild('sidebar') sidebar!: SidebarComponent;
  sidebarCollapsed = false;

  constructor(
    public authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    // Check initial route
    this.checkRoute(this.router.url);
    
    // Listen for route changes
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: NavigationEnd) => {
      this.checkRoute(event.urlAfterRedirects || event.url);
    });
  }

  private checkRoute(url: string): void {
    // Check if current route is login or root
    this.isLoginRoute = url === '/login' || url === '/' || url === '';
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
  
  menuItems: MenuItem[] = [
    {
      id: 'dashboard',
      label: 'Dashboard',
      icon: '📊',
      route: '/dashboard'
    },
    {
      id: 'properties',
      label: 'Properties',
      icon: '🏠',
      expanded: false,
      children: [
        {
          id: 'properties-list',
          label: 'Properties Register',
          route: '/properties'
        },
        {
          id: 'new-property',
          label: 'Add new property',
          route: 'properties/add'
        },
      ]
    },
    {
      id: 'landlords',
      label: 'Landlords',
      icon: '👥',
      expanded: false,
      children: [
        {
          id: 'landlords-list',
          label: 'Landlords Register',
          route: '/landlords'
        },
        {
          id: 'add-landlord',
          label: 'Add new landlord',
          route: '/landlords/add'
        }
      ]
    },
    {
      id: 'users',
      label: 'Users',
      icon: '👤',
      expanded: false,
      children: [
        {
          id: 'users-list',
          label: 'Users Register',
          route: '/users'
        },
        {
          id: 'new-user',
          label: 'Add new user',
          route: '/users/add'
        }
      ]
    },
    {
      id: 'reports',
      label: 'Reports',
      icon: '📝',
      expanded: false,
      children: [
        {
          id: 'financial-reports',
          label: 'Financial Reports',
          route: '/reports'
        },
        {
          id: 'analytics',
          label: 'Analytics',
          expanded: false,
          children: [
            {
              id: 'monthly-analytics',
              label: 'Monthly Analytics',
              route: '/reports/analytics/monthly'
            },
            {
              id: 'quarterly-analytics',
              label: 'Quarterly Analytics',
              route: '/reports/analytics/quarterly'
            }
          ]
        }
      ]
    },
    {
      id: 'settings',
      label: 'Settings',
      icon: '⚙️',
      route: '/settings'
    }
  ];
}

