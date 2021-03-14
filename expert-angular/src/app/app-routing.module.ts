import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DocumentComponent } from 'src/app/components/document/document.component';
import { DocumentDetailComponent } from 'src/app/components/document-detail/document-detail.component';

const routes: Routes = [
  { path: 'documents', component: DocumentComponent },
  { path: 'detail/:id', component: DocumentDetailComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
