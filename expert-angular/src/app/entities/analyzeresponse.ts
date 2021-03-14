
    export class Property {
        type: string;
        value: string;
    }

    export class Knowledge {
        syncon: number;
        label: string;
        properties: Property[];
    }

    export class Dependency {
        id: number;
        head: number;
        label: string;
    }

    export class Atom {
        start: number;
        end: number;
        type: string;
        lemma: string;
    }

    export class Vsyn {
        id: number;
        parent: number;
    }

    export class Token {
        start: number;
        end: number;
        syncon: number;
        type: string;
        pos: string;
        lemma: string;
        dependency: Dependency;
        morphology: string;
        paragraph: number;
        sentence: number;
        phrase: number;
        atoms: Atom[];
        vsyn: Vsyn;
    }

    export class Phras {
        start: number;
        end: number;
        tokens: number[];
        type: string;
    }

    export class Sentence {
        start: number;
        end: number;
        phrases: number[];
    }

    export class Paragraph {
        start: number;
        end: number;
        sentences: number[];
    }

    export class MainSentence {
        start: number;
        end: number;
        value: string;
        score: number;
    }

    export class MainPhras {
        value: string;
        score: number;
        positions: Position[];
    }

    export class Position2 {
        start: number;
        end: number;
    }

    export class MainLemma {
        value: string;
        score: number;
        positions: Position2[];
    }

    export class Position3 {
        start: number;
        end: number;
    }

    export class MainSyncon {
        syncon: number;
        lemma: string;
        score: number;
        positions: Position3[];
    }

    export class Topic {
        id: number;
        label: string;
        score: number;
        winner: boolean;
    }

    export class Position {
        start: number;
        end: number;
    }

    export class Attribute3 {
        attribute: string;
        lemma: string;
        syncon: number;
        type: string;
        attributes?: any;
    }

    export class Attribute2 {
        attribute: string;
        lemma: string;
        syncon: number;
        type: string;
        attributes: Attribute3[];
    }

    export class Attribute {
        attribute: string;
        lemma: string;
        syncon: number;
        type: string;
        attributes: Attribute2[];
    }

    export class Entity {
        syncon: number;
        type: string;
        lemma: string;
        relevance?: number;
        positions: Position[];
        attributes: Attribute[];
    }

    export class Data {
        language: string;
        version: string;
        knowledge?: Knowledge[];
        tokens?: Token[];
        phrases?: Phras[];
        sentences?: Sentence[];
        paragraphs?: Paragraph[];
        mainSentences?: MainSentence[];
        mainPhrases?: MainPhras[];
        mainLemmas?: MainLemma[];
        mainSyncons?: MainSyncon[];
        topics?: any;
        entities?: Entity[];
        relations?: any;
        sentiment?: any;
        categories?: any;
        extractions?: any;
    }

    export class AnalyzeResponse {
        data?: Data;
    }


