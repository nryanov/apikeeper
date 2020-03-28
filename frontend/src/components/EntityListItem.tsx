import React from "react"
import {EntityProps} from "../logic/types";
import {useDispatch} from "react-redux";
import * as action from "../logic/action";


const EntityListItem: React.FunctionComponent<EntityProps> = (props) => {
    const dispatch = useDispatch();

    const selectEntity = () => {
        dispatch(action.apiCalls.selectEntity(props.id))
    };

    return (
        <tr>
            <td onClick={selectEntity}>
                <p><b>[{props.entityType}]</b> {props.name}</p>
            </td>
        </tr>
    )
};

export default EntityListItem;