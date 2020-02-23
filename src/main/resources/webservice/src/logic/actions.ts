import {Branch, EntityProps, Id} from "../common/Types";
import {
    CREATE_ENTITIES,
    CREATE_ENTITY,
    CREATE_RELATION,
    CREATE_RELATIONS,
    FIND_CLOSEST_ENTITY_RELATIONS,
    FIND_ENTITIES,
    FIND_ENTITY,
    KeeperActions,
    REMOVE_ALL_ENTITY_RELATIONS,
    REMOVE_ENTITY,
    REMOVE_RELATION,
    REMOVE_RELATIONS
} from "./actionTypes";

export function createEntities(entitiesProps: EntityProps[]): KeeperActions {
  return {
      type: CREATE_ENTITIES,
      entitiesProps: entitiesProps
  }
}

export function createEntity(entityProps: EntityProps): KeeperActions {
    return {
        type: CREATE_ENTITY,
        entityProps: entityProps
    }
}

export function findEntities(ids: Id[]): KeeperActions {
    return {
        type: FIND_ENTITIES,
        ids: ids
    }
}

export function findEntity(id: Id): KeeperActions {
    return {
        type: FIND_ENTITY,
        id: id
    }
}

export function findClosestEntityRelations(id: Id): KeeperActions {
    return {
        type: FIND_CLOSEST_ENTITY_RELATIONS,
        id: id
    }

}

export function createRelation(branch: Branch): KeeperActions {
    return {
        type: CREATE_RELATION,
        branch: branch
    }
}

export function createRelations(branches: Branch[]): KeeperActions {
    return {
        type: CREATE_RELATIONS,
        branches: branches
    }
}

export function removeEntity(id: Id): KeeperActions {
    return {
        type: REMOVE_ENTITY,
        id: id
    }
}

export function removeRelation(id: Id): KeeperActions {
    return {
        type: REMOVE_RELATION,
        id: id
    }
}

export function removeRelations(ids: Id[]): KeeperActions {
    return {
        type: REMOVE_RELATIONS,
        ids: ids
    }
}

export function removeAllEntityRelations(id: Id): KeeperActions {
    return {
        type: REMOVE_ALL_ENTITY_RELATIONS,
        id: id
    }
}