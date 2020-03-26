import {BranchDef, EntityDef, EntityProps, Id, LeafFull, RelationProps} from "./types";
import {
    CREATE_ENTITY,
    CREATE_RELATION,
    FIND_CLOSEST_ENTITY_RELATIONS,
    FIND_ENTITIES,
    FIND_ENTITY,
    REMOVE_ALL_ENTITY_RELATIONS,
    REMOVE_ENTITY,
    REMOVE_RELATION,
    REMOVE_RELATIONS, UPDATE_ENTITY,
    SELECT_ENTITY
} from "./actionType";
import {Dispatch} from "redux";
import * as api from "./api"

export const actionCalls = {
    createEntity: (entityProps: EntityProps) => {
        return {
            type: CREATE_ENTITY,
            entityProps: entityProps
        }
    },

    updateEntity: (entityProps: EntityProps) => {
        return {
            type: UPDATE_ENTITY,
            entityProps: entityProps
        }
    },

    findEntities: (entityProps: EntityProps[]) => {
        return {
            type: FIND_ENTITIES,
            entityProps: entityProps
        }
    },

    findEntity: (entityProps: EntityProps) => {
        return {
            type: FIND_ENTITY,
            entityProps: entityProps
        }
    },

    findClosestEntityRelations: (id: Id, leafs: LeafFull[]) => {
        return {
            type: FIND_CLOSEST_ENTITY_RELATIONS,
            entityId: id,
            relations: leafs
        }
    },

    createRelation: (branchDef: BranchDef, relationProps: RelationProps) => {
        return {
            type: CREATE_RELATION,
            branchDef: branchDef,
            relation: relationProps
        }
    },

    removeEntity: (id: Id) => {
        return {
            type: REMOVE_ENTITY,
            entityId: id
        }
    },

    removeRelation: (id: Id) => {
        return {
            type: REMOVE_RELATION,
            relationId: id
        }
    },

    removeRelations: (ids: Id[]) => {
        return {
            type: REMOVE_RELATIONS,
            relationIds: ids
        }
    },

    removeAllEntityRelations: (id: Id) => {
        return {
            type: REMOVE_ALL_ENTITY_RELATIONS,
            entityId: id
        }
    },

    selectEntity: (id: Id) => {
        return {
            type: SELECT_ENTITY,
            entityId: id
        }
    },
};

export const apiCalls = {
    createEntity: (entityDef: EntityDef) => (dispatch: Dispatch<any>) => {
        return api.createEntity(entityDef)
            .then(data => dispatch(actionCalls.createEntity(data)));
    },

    updateEntity: (entity: EntityProps) => (dispatch: Dispatch<any>) => {
        return api.updateEntity(entity)
            .then(data => dispatch(actionCalls.updateEntity(data)));
    },

    findEntities: (page: number, entries: number) => (dispatch: Dispatch) => {
        return api.findEntities(page, entries)
            .then(data => dispatch(actionCalls.findEntities(data)));
    },

    findEntity: (id: Id) => (dispatch: Dispatch) => {
        return api.findEntity(id)
            .then(data => dispatch(actionCalls.findEntity(data)));
    },

    findClosestEntityRelations: (id: Id) => (dispatch: Dispatch) => {
        return api.findClosestEntityRelations(id)
            .then(data => dispatch(actionCalls.findClosestEntityRelations(id, data)));
    },

    createRelation: (branchDef: BranchDef) => (dispatch: Dispatch) => {
        return api.createRelation(branchDef)
            .then(data => dispatch(actionCalls.createRelation(branchDef, data)));
    },

    removeEntity: (id: Id) => (dispatch: Dispatch) => {
        return api.removeEntity(id)
            .then(() => dispatch(actionCalls.removeEntity(id)));
    },

    removeRelation: (id: Id) => (dispatch: Dispatch) => {
        return api.removeRelation(id)
            .then(() => dispatch(actionCalls.removeRelation(id)));
    },

    removeRelations: (ids: Id[]) => (dispatch: Dispatch) => {
        return api.removeRelations(ids)
            .then(() => dispatch(actionCalls.removeRelations(ids)));
    },

    removeAllEntityRelations: (id: Id) => (dispatch: Dispatch) => {
        return api.removeAllEntityRelations(id)
            .then(() => dispatch(actionCalls.removeAllEntityRelations(id)));
    },

    selectEntity: (id: Id) => {
        return actionCalls.selectEntity(id);
    },
};
