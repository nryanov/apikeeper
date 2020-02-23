import {Branch, EntityProps, Id} from "../common/Types";

export const FIND_ENTITIES = "FIND_ENTITIES";
export const FIND_ENTITY = "FIND_ENTITY";
export const CREATE_ENTITY = "CREATE_ENTITY";
export const CREATE_ENTITIES = "CREATE_ENTITIES";
export const CREATE_RELATION = "CREATE_RELATION";
export const CREATE_RELATIONS = "CREATE_RELATIONS";
export const FIND_CLOSEST_ENTITY_RELATIONS = "FIND_CLOSEST_ENTITY_RELATIONS";
export const REMOVE_ENTITY = "REMOVE_ENTITY";
export const REMOVE_RELATION = "REMOVE_RELATION";
export const REMOVE_RELATIONS = "REMOVE_RELATIONS";
export const REMOVE_ALL_ENTITY_RELATIONS = "REMOVE_ALL_ENTITY_RELATIONS";

type FindEntitiesAction = {
    type: typeof FIND_ENTITIES
    ids: Id[]
}

type FindEntityAction = {
    type: typeof FIND_ENTITY;
    id: Id;
}

type CreateEntityAction = {
    type: typeof CREATE_ENTITY;
    entityProps: EntityProps;
}

type CreateEntitiesAction = {
    type: typeof CREATE_ENTITIES;
    entitiesProps: EntityProps[];
}

type CreateRelationAction = {
    type: typeof CREATE_RELATION;
    branch: Branch;
}

type CreateRelationsAction = {
    type: typeof CREATE_RELATIONS;
    branches: Branch[];
}

type FindClosestEntityRelationsAction = {
    type: typeof FIND_CLOSEST_ENTITY_RELATIONS;
    id: Id;
}

type RemoveEntityAction = {
    type: typeof REMOVE_ENTITY;
    id: Id;
}

type RemoveRelationAction = {
    type: typeof REMOVE_RELATION;
    id: Id;
}

type RemoveRelationsAction = {
    type: typeof REMOVE_RELATIONS;
    ids: Id[];
}

type RemoveAllEntityRelationsAction = {
    type: typeof REMOVE_ALL_ENTITY_RELATIONS;
    id: Id;
}

export type KeeperActions =
    FindEntitiesAction
    | FindEntityAction
    | CreateEntityAction
    | CreateEntitiesAction
    | CreateRelationAction
    | CreateRelationsAction
    | FindClosestEntityRelationsAction
    | RemoveEntityAction
    | RemoveRelationAction
    | RemoveRelationsAction
    | RemoveAllEntityRelationsAction;
