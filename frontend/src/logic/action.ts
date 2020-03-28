import {
    BranchDef, Dispatcher,
    EntityDef,
    EntityProps,
    EntityType,
    Id,
    KeeperActions,
    LeafFull,
    RelationProps, Result,
    State
} from "./types";
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
    SELECT_ENTITY,
    CHANGE_PAGE,
    FILTER_ENTITIES
} from "./actionType";
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

    changePage: (page: number) => {
        return {
            type: CHANGE_PAGE,
            page: page
        }
    },

    filterEntities: (namePattern: string | null, entityType: EntityType | null) => {
        return {
            type: FILTER_ENTITIES,
            namePattern: namePattern,
            entityType: entityType
        }
    },
};

export const apiCalls = {
    createEntity: (entityDef: EntityDef): Result<Promise<KeeperActions>, EntityDef> => (dispatch: Dispatcher<EntityDef>) => {
        return api.createEntity(entityDef)
            .then(data => dispatch(actionCalls.createEntity(data)));
    },

    updateEntity: (entity: EntityProps): Result<Promise<KeeperActions>, EntityProps> => (dispatch: Dispatcher<EntityProps>) => {
        return api.updateEntity(entity)
            .then(data => dispatch(actionCalls.updateEntity(data)));
    },

    findAllEntities: (): Result<Promise<KeeperActions>, undefined> => (dispatch: Dispatcher<undefined>) => {
        return api.findAllEntities()
            .then(data => dispatch(actionCalls.findEntities(data)));
    },

    findEntities: (page: number, entries: number): Result<Promise<KeeperActions>, [number, number]> => (dispatch: Dispatcher<[number, number]>) => {
        return api.findEntities(page, entries)
            .then(data => dispatch(actionCalls.findEntities(data)));
    },

    findEntity: (id: Id): Result<Promise<KeeperActions>, Id> => (dispatch: Dispatcher<Id>) => {
        return api.findEntity(id)
            .then(data => dispatch(actionCalls.findEntity(data)));
    },

    findClosestEntityRelations: (id: Id): Result<Promise<KeeperActions>, Id> => (dispatch: Dispatcher<Id>) => {
        return api.findClosestEntityRelations(id)
            .then(data => dispatch(actionCalls.findClosestEntityRelations(id, data)));
    },

    createRelation: (branchDef: BranchDef): Result<Promise<KeeperActions>, BranchDef> => (dispatch: Dispatcher<BranchDef>) => {
        return api.createRelation(branchDef)
            .then(data => dispatch(actionCalls.createRelation(branchDef, data)));
    },

    removeEntity: (id: Id): Result<Promise<KeeperActions>, Id> => (dispatch: Dispatcher<Id>) => {
        return api.removeEntity(id)
            .then(() => dispatch(actionCalls.removeEntity(id)));
    },

    removeRelation: (id: Id): Result<Promise<KeeperActions>, Id> => (dispatch: Dispatcher<Id>) => {
        return api.removeRelation(id)
            .then(() => dispatch(actionCalls.removeRelation(id)));
    },

    removeRelations: (ids: Id[]): Result<Promise<KeeperActions>, Id[]> => (dispatch: Dispatcher<Id[]>) => {
        return api.removeRelations(ids)
            .then(() => dispatch(actionCalls.removeRelations(ids)));
    },

    removeAllEntityRelations: (id: Id): Result<Promise<KeeperActions>, Id> => (dispatch: Dispatcher<Id>) => {
        return api.removeAllEntityRelations(id)
            .then(() => dispatch(actionCalls.removeAllEntityRelations(id)));
    },

    // @ts-ignore
    selectEntity: (id: Id): Result<Promise<KeeperActions>, Id> => (dispatch: Dispatcher<Id>, getState: () => State) => {
        if (getState().entityStates[id]) {
            return dispatch(actionCalls.selectEntity(id));
        } else {
            return dispatch(apiCalls.findClosestEntityRelations(id)).then(() => dispatch(actionCalls.selectEntity(id)));
        }
    },
};
