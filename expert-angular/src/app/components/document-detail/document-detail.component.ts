import { Component, OnInit } from '@angular/core';
import { HttpEventType, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DocumentService} from 'src/app/services/document.service';
import { Document } from 'src/app/entities/document';
import { ActivatedRoute } from '@angular/router';
import { Location } from '@angular/common';
import { Category } from 'src/app/entities/category';
import { Categorization } from 'src/app/entities/categorization';
import { Fullanalysis } from 'src/app/entities/fullanalysis';


@Component({
  selector: 'app-document-detail',
  templateUrl: './document-detail.component.html',
  styleUrls: ['./document-detail.component.scss']
})
export class DocumentDetailComponent implements OnInit {

  message: string[];
  id: string;
  document?: Document;
  content?: string;
  categories?: Category[];
  categorizations?: Categorization[];
  fullAnalysis?: Fullanalysis[];

  constructor(
    private route: ActivatedRoute,
    private location: Location,
    private documentService: DocumentService) { }

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('id');
    this.getDocument();
    this.getDocumentContent();
    this.getCategorizations();
    this.getFullAnalysis();
    //this.getFull2();

  }

  getDocument(): void {
    this.documentService.getDocument(this.id).subscribe( 
      (res) => { this.document = res},
    (err) => console.log(err),
    () => console.log('done!'));
  }
  getDocumentContent(): void {
    this.documentService.getDocumentContent(this.id).subscribe( 
      (res) => { this.content = res},
    (err) => console.log(err),
    () => console.log('done!'));
  }

  getCategorizations(): void {
    this.documentService.getCategorizations(this.id).subscribe(
      (res) => {this.categorizations = res},
      (err) => console.log(err),
      () => console.log('done!'));
  }

  getFullAnalysis(): void {
    this.documentService.getFullAnalysis(this.id).subscribe(
      (res) => {this.fullAnalysis = res; console.log(res)},
      (err) => console.log(err),
      () => console.log('done!'));
  }

  categorize(): void {
    this.documentService.categorize(this.id).subscribe(
      (event: any) => {
        if (event instanceof HttpResponse) {
          const msg = 'Categorization successfully: ';
          this.message.push(msg);
        }
        this.getCategorizations();
      },
      (err: any) => {
        const msg = 'Could not categorize it ';
        this.message.push(msg);
      });
  }

  analyze(): void {
    this.documentService.analyze(this.id).subscribe(
      (event: any) => {
        if (event instanceof HttpResponse) {
          const msg = 'Full analysis successfully: ';
          this.message.push(msg);
        }
        this.getFullAnalysis();
      },
      (err: any) => {
        const msg = 'Could not analyze it ';
        this.message.push(msg);
      });
  }
}
