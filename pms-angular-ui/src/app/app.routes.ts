import { Routes } from '@angular/router';
import { authGuard } from './guards/auth.guard';

import { LoginComponent } from './components/login/login.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { PropertiesComponent } from './components/properties/properties.component';
import { UsersComponent } from './components/users/users.component';
import { LandlordComponent } from './components/landlords/landlords.component';
import { AddLandlordComponent } from './components/landlords/add-landlord/add-landlord.component';
import { ReportsComponent } from './components/reports/reports.component';
import { AddPropertyComponent } from './components/properties/add-property/add-property.component';
import { UpdatePropertyComponent } from './components/properties/update-property/update-property.component';
import { AddUserComponent } from './components/users/add-user/add-user.component';

export const routes: Routes = [
  { 
    path: 'login',
    component: LoginComponent 
  },

  { 
    path: 'dashboard', 
    component: DashboardComponent, 
    canActivate: [authGuard] 
  },

  { 
    path: '', 
    redirectTo: '/login', 
    pathMatch: 'full' 
  },



  // Properties routes
  { 
    path: 'properties', 
    component: PropertiesComponent, 
    canActivate: [authGuard] 
  },

  {
    path: 'properties/add',
    component: AddPropertyComponent,
    canActivate: [authGuard]
  },

  {
    path: 'properties/edit/:id',
    component: UpdatePropertyComponent,
    canActivate: [authGuard]
  },



  // Landlords routes
  { 
    path: 'landlords', 
    component: LandlordComponent, 
    canActivate: [authGuard] 
  },

  {
    path: 'landlords/add',
    component: AddLandlordComponent,
    canActivate: [authGuard]
  },



  // Users routes
  { 
    path: 'users', 
    component: UsersComponent, 
    canActivate: [authGuard] 
  },

  { 
    path: 'users/add', 
    component: AddUserComponent, 
    canActivate: [authGuard] 
  },


  // Reports routes
  {
    path: 'reports',
    component: ReportsComponent,
    canActivate: [authGuard]
  }

];
