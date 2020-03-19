import {BranchDef, EntityDef, EntityProps, Id, Leaf, RelationProps} from "./types";
import {
    CREATE_ENTITY,
    CREATE_RELATION,
    FIND_CLOSEST_ENTITY_RELATIONS,
    FIND_ENTITIES,
    FIND_ENTITY,
    REMOVE_ALL_ENTITY_RELATIONS,
    REMOVE_ENTITY,
    REMOVE_RELATION,
    REMOVE_RELATIONS
} from "./actionType";
import {Dispatch} from "redux";
import * as api from './api'

export namespace actionCalls {
    export const createEntity = (entityProps: EntityProps) => {
        return {
            type: CREATE_ENTITY,
            entityProps: entityProps
        }
    };

    export const findEntities = (entityProps: EntityProps[]) => {
        return {
            type: FIND_ENTITIES,
            entityProps: entityProps
        }
    };

    export const findEntity = (entityProps: EntityProps) => {
        return {
            type: FIND_ENTITY,
            entityProps: entityProps
        }
    };

    export const findClosestEntityRelations = (id: Id, leafs: Leaf[]) => {
        return {
            type: FIND_CLOSEST_ENTITY_RELATIONS,
            entityId: id,
            relations: leafs
        }
    };

    export const createRelation = (branchDef: BranchDef, relationProps: RelationProps) => {
        return {
            type: CREATE_RELATION,
            branchDef: branchDef,
            relation: relationProps
        }
    };

    export const removeEntity = (id: Id) => {
        return {
            type: REMOVE_ENTITY,
            entityId: id
        }
    };

    export const removeRelation = (id: Id) => {
        return {
            type: REMOVE_RELATION,
            relationId: id
        }
    };

    export const removeRelations = (ids: Id[]) => {
        return {
            type: REMOVE_RELATIONS,
            relationIds: ids
        }
    };

    export const removeAllEntityRelations = (id: Id) => {
        return {
            type: REMOVE_ALL_ENTITY_RELATIONS,
            entityId: id
        }
    };
}

export namespace apiCalls {
    export const createEntity = (entityDef: EntityDef) => (dispatch: Dispatch<any>) => {
        return api.createEntity(entityDef)
            .then(data => dispatch(actionCalls.createEntity(data)));
    };

    export const findEntities = (page: number, entries: number) => (dispatch: Dispatch) => {
        return api.findEntities(page, entries)
            .then(data => dispatch(actionCalls.findEntities(data)));
    };

    export const findEntity = (id: Id) => (dispatch: Dispatch) => {
        return api.findEntity(id)
            .then(data => dispatch(actionCalls.findEntity(data)));
    };

    export const findClosestEntityRelations = (id: Id) => (dispatch: Dispatch) => {
        return api.findClosestEntityRelations(id)
            .then(data => dispatch(actionCalls.findClosestEntityRelations(id, data)));
    };

    export const createRelation = (branchDef: BranchDef) => (dispatch: Dispatch) => {
        return api.createRelation(branchDef)
            .then(data => dispatch(actionCalls.createRelation(branchDef, data)));
    };

    export const removeEntity = (id: Id) => (dispatch: Dispatch) => {
        return api.removeEntity(id)
            .then(() => dispatch(actionCalls.removeEntity(id)));
    };

    export const removeRelation = (id: Id) => (dispatch: Dispatch) => {
        return api.removeRelation(id)
            .then(() => dispatch(actionCalls.removeRelation(id)));
    };

    export const removeRelations = (ids: Id[]) => (dispatch: Dispatch) => {
        return api.removeRelations(ids)
            .then(() => dispatch(actionCalls.removeRelations(ids)));
    };

    export const removeAllEntityRelations = (id: Id) => (dispatch: Dispatch) => {
        return api.removeAllEntityRelations(id)
            .then(() => dispatch(actionCalls.removeAllEntityRelations(id)));
    };
}