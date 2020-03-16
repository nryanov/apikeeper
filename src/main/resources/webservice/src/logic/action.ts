import {Branch, EntityProps, Id} from "./types";
import {
    CREATE_ENTITIES,
    CREATE_ENTITY,
    CREATE_RELATION,
    CREATE_RELATIONS,
    FIND_CLOSEST_ENTITY_RELATIONS,
    FIND_ENTITIES,
    FIND_ENTITY,
    REMOVE_ALL_ENTITY_RELATIONS,
    REMOVE_ENTITY,
    REMOVE_RELATION,
    REMOVE_RELATIONS
} from "./actionType";

export const createEntities = (entitiesProps: EntityProps[]) => {
  return {
      type: CREATE_ENTITIES,
      entitiesProps: entitiesProps
  }
};

export const createEntity = (entityProps: EntityProps) => {
    return {
        type: CREATE_ENTITY,
        entityProps: entityProps
    }
};

export const findEntities = (ids: Id[]) => {
    return {
        type: FIND_ENTITIES,
        ids: ids
    }
};

export const findEntity = (id: Id) => {
    return {
        type: FIND_ENTITY,
        id: id
    }
};

export const findClosestEntityRelations = (id: Id) => {
    return {
        type: FIND_CLOSEST_ENTITY_RELATIONS,
        id: id
    }

};

export const createRelation = (branch: Branch) => {
    return {
        type: CREATE_RELATION,
        branch: branch
    }
};

export const createRelations = (branches: Branch[]) => {
    return {
        type: CREATE_RELATIONS,
        branches: branches
    }
};

export const removeEntity = (id: Id) => {
    return {
        type: REMOVE_ENTITY,
        id: id
    }
};

export const removeRelation = (id: Id) => {
    return {
        type: REMOVE_RELATION,
        id: id
    }
};

export const removeRelations = (ids: Id[]) => {
    return {
        type: REMOVE_RELATIONS,
        ids: ids
    }
};

export const removeAllEntityRelations = (id: Id) => {
    return {
        type: REMOVE_ALL_ENTITY_RELATIONS,
        id: id
    }
};
