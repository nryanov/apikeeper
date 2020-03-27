import * as actions from "./action";

export type EntityType = "Service" | "Storage" | "MessageQueue";

export type RelationType = "Downstream" | "Upstream";

/**
 * UUID representation
 */
export type Id = string

/**
 * Definition of entity without id. This type is used to create an entity using API
 */
export type EntityDef = {
    readonly type: EntityType;
    readonly name: string;
    readonly description: string | null;
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
 * relation - relation info
 * entity - connected entity id
 */
export type Leaf = {
    readonly relation: RelationProps,
    readonly targetEntity: Id;
}

/**
 * Full information about current entity connections.
 * id - source entity
 * relation - relation info
 * entity - connected entity
 */
export type LeafFull = {
    readonly id: Id;
    readonly relation: RelationProps,
    readonly entity: EntityProps;
}

/**
 * Application state.
 */
export type State = {
    readonly entityProps: {[key: string]: EntityProps};
    readonly entityStates: {[key: string]: Leaf[]};
    readonly selectedEntity: Id | null;

    readonly page: number;
    readonly maxPage: number;
    readonly filterByName: string | null;
    readonly filterByType: EntityType | null;
    readonly filteredEntityProps: {[key: string]: EntityProps};
}

export const MAX_PAGE_SIZE = 5;

type InferType<T> = T extends { [key: string]: infer U} ? U : never;

export type KeeperActions = ReturnType<InferType<typeof actions.actionCalls>>
