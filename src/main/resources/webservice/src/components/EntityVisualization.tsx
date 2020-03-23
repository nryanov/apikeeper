import React from "react"
import {EntityProps, Leaf, State} from "../logic/types";
import {useSelector} from "react-redux";
import d3 from "d3"


const EntityVisualization: React.FunctionComponent<EntityProps> = (props) => {
    const leafs = useSelector<State, Leaf[]>(state => state.entityStates[props.id]);

    return (
        <div></div>
    )
};

export default EntityVisualization;