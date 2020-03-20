import * as actions from "./action";

export type EntityType = {
    readonly type : 'Service' | 'Storage' | 'MessageQueue';
}

export type RelationType = {
    readonly type : 'In' | 'Out';
}

/**
 * UUID representation
 */
export type Id = {
    readonly id: string;
}

/**
 * Definition of entity without id. This type is used to create an entity using API
 */
export type EntityDef = {
    readonly type: EntityType;
    readonly name: string;
    readonly description: String | null;
}

/**
 * Definition of relation without id. This type is used to create a relation in BranchDef using API
 */
export type RelationDef = {
    readonly relationType: RelationType;
}

/**
 * Definition of branch with RelationDef instead of Relation. This type is used to create a relation using API
 */
export type BranchDef = {
    readonly left: Id;
    readonly relation: RelationDef;
    readonly right: Id;
}

/**
 * Full entity properties
 */
export type EntityProps = {
    readonly id: Id;
    readonly type: EntityType;
    readonly name: string;
    readonly description: string | null;
}

/**
 * Full relation properties
 */
export type RelationProps = {
    readonly id: Id;
    readonly relationType: RelationType;
}

/**
 * Full information about current entity connections.
 * id - source entity
 * relation - relation info
 * entity - connected entity
 */
export type Leaf = {
    readonly id: Id;
    readonly relation: RelationProps,
    readonly entity: EntityProps;
}

/**
 * Full information about branch.
 * left - entity info
 * relation - relation info
 * right - entity info
 */
export type Branch = {
    readonly left: EntityProps;
    readonly relation: RelationProps;
    readonly right: EntityProps;
}


/**
 * Single entity state.
 * entityProps - entity properties
 * leafs - information about all closest (1N) connections
 */
export type EntityState = {
    readonly entityProps: EntityProps;
    readonly leafs: Leaf[]
}

/**
 * Application state.
 */
export type State = {
    readonly entityProps: EntityProps[];
    readonly entityStates: {[key: string]: EntityState}
}

type InferType<T> = T extends { [key: string]: infer U} ? U : never;

export type KeeperActions = ReturnType<InferType<typeof actions.actionCalls>>
