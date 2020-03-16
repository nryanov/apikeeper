import * as actions from "./action";

export type EntityType = {
    readonly type : 'service' | 'storage' | 'messageQueue';
}

export type RelationType = {
    readonly type : 'in' | 'out';
}

export type Id = {
    readonly id: String;
}

export type EntityProps = {
    readonly id: Id;
    readonly type: EntityType;
    readonly name: String;
    readonly description: String | null;
    readonly wikiLink: String | null;
}

export type RelationProps = {
    readonly id: Id;
    readonly relationType: RelationType;
}

export type Leaf = {
    readonly id: Id;
    readonly relation: RelationProps,
    readonly entity: EntityProps;
}

export type Branch = {
    readonly left: EntityProps;
    readonly relation: RelationProps;
    readonly right: EntityProps;
}

export type EntityState = {
    readonly entityProps: EntityProps;
    readonly leafs: Leaf[]
}

export type State = {
    readonly entityNames: string[];
    readonly entityProps: EntityProps[];
    readonly entityStates: {[key: string]: EntityState}
}

type InferType<T> = T extends { [key: string]: infer U} ? U : never;

export type KeeperActions = ReturnType<InferType<typeof actions>>
