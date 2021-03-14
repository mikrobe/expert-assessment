import { Category } from "./category";
import { Document } from "./document";

export class Categorization {
    uid: number;
    document: Document;
    documentCategories: Category[];
}
