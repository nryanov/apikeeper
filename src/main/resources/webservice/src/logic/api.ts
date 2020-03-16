import axios from 'axios'
import {EntityProps, Id} from "./types";

const apiBase = "";
const apiVersion = "v1";
const apiPath = `${apiBase}/${apiVersion}`;

//todo: page=<number>&entries=<number>
export const findEntities = () => axios.get<any, EntityProps[]>(`${apiPath}/entity`);

export const findEntity = (id: Id) => axios.get<any, EntityProps>("");

export const createEntity = (props: EntityProps) => {};

export const createEntities = () => {};

export const createRelation = () => {};

export const createRelations = () => {};

export const findClosestEntityRelations = (id: Id) => axios.get<any, EntityProps>("");

export const removeEntity = () => {};

export const removeRelation = () => {};

export const removeRelations = () => {};

export const removeAllEntityRelations = () => {};
