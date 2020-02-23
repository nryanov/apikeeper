import {createStore, applyMiddleware} from "redux";
import thunk from 'redux-thunk';
import {EntityProps} from "../common/Types";

export type KeeperState = {
    entities: EntityProps[]
}