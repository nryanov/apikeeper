import React from "react"
import {connect} from "react-redux";
import {EntityProps, EntityType, Id, RelationType, State} from "../logic/types";
import * as d3 from "d3"
import { SimulationNodeDatum } from "d3";
import _ from "lodash"

function linkArc(d: any) {
    const r = Math.hypot(d.target.x - d.source.x, d.target.y - d.source.y);
    return `M${d.source.x},${d.source.y} A${r},${r} 0 0,1 ${d.target.x},${d.target.y}`;
}

interface Node extends SimulationNodeDatum {
    readonly id: Id;
    readonly name: string;
    readonly type: EntityType;
}

interface Link {
    readonly id: Id;
    readonly source: Id;
    readonly target: Id;
    readonly relationType: RelationType;
}

interface LocalProps {
    readonly nodes: Node[],
    readonly links: Link[]
}

class EntityVisualizationComponent extends React.Component<LocalProps> {
    private svgRef: React.RefObject<SVGSVGElement> = React.createRef();
    private types = ["Service", "Storage", "MessageQueue", "Downstream", "Upstream"];
    private connectionTypes = ["Downstream", "Upstream"];
    private color: any = d3.scaleOrdinal().domain(this.types).range(d3.schemePastel2);
    private simulation: any = null;

    componentDidMount(): void {
        this.updateGraph();
    }

    componentDidUpdate(prevProps: Readonly<LocalProps>, prevState: Readonly<{}>, snapshot?: any): void {
        this.updateGraph();
    }

    updateGraph() {
        if (this.simulation !== null) {
            this.simulation.stop()
        }

        this.simulation = d3.forceSimulation(this.props.nodes)
            .force("link", d3.forceLink(this.props.links).id(node => (node as Node).id))
            .force("charge", d3.forceManyBody().strength(-400))
            .force("center_force", d3.forceCenter(800 / 2, 150 / 2)); // constants >_<

        const svg = d3.select(this.svgRef.current);
        svg.selectAll("*").remove();

        svg.append("defs").selectAll("marker")
            .data(this.connectionTypes)
            .join("marker")
            .attr("id", d => `arrow-${d}`)
            .attr("viewBox", "0 -5 10 10")
            .attr("refX", 15)
            .attr("refY", -0.5)
            .attr("markerWidth", 6)
            .attr("markerHeight", 6)
            .attr("orient", "auto")
            .append("path")
            .attr("fill", this.color)
            .attr("d", "M0,-5L10,0L0,5");

        const link = svg.append("g")
            .attr("fill", "none")
            .attr("stroke-width", 1.5)
            .selectAll("path")
            .data(this.props.links)
            .join("path")
            .attr("stroke", d => this.color(d.relationType))
            .attr("marker-end", d => `url(#arrow-${d.relationType})`);

        const node = svg.append("g")
            .attr("fill", "currentColor")
            .attr("stroke-linecap", "round")
            .attr("stroke-linejoin", "round")
            .selectAll("g")
            .data(this.props.nodes)
            .join("g");

        node.append("circle")
            .attr("stroke", "white")
            .attr("fill", d => this.color(d.type))
            .attr("stroke-width", 1.5)
            .attr("r", 5);

        node.append("text")
            .attr("x", 12)
            .attr("y", "0.31em")
            .text(d => d.name)
            .clone(true).lower()
            .attr("stroke", "white")
            .attr("stroke-width", 3);

        this.simulation.on("tick", () => {
            link.attr("d", linkArc);
            node.attr("transform", d => `translate(${d.x},${d.y})`);
        });
    }

    render() {
        return (
            <div className="mt-4">
                <div>
                    <span className="badge badge-pill badge-primary">Service</span>
                    <span className="badge badge-pill badge-secondary">Storage</span>
                    <span className="badge badge-pill badge-success">MessageQueue</span>
                </div>
                <div>
                    <span className="badge badge-pill badge-danger">Downstream</span>
                    <span className="badge badge-pill badge-warning">Upstream</span>
                </div>
                <svg width="90%" height="90%" ref={this.svgRef}>

                </svg>
            </div>
        );
    }
}

const mapStateToProps = (state: State): LocalProps => {
    const selectedEntity = state.selectedEntity;

    if (selectedEntity !== null) {
        const nodesWithLink: [Node, Link][] = _.map(state.entityStates[selectedEntity], leaf => {
            const node: Node = state.entityProps[leaf.targetEntity];
            const link: Link = leaf.relation.relationType === "Downstream" ? {
                ...leaf.relation,
                source: selectedEntity,
                target: leaf.targetEntity
            } : {
                ...leaf.relation,
                source: leaf.targetEntity,
                target: selectedEntity
            };

            return [node, link];
        });

        const [nodes, links] = _.unzip(nodesWithLink);
        nodes.push(state.entityProps[selectedEntity]);

        return {
            nodes: nodes as Node[],
            links: links as Link[]
        };
    } else {
        return {
            nodes: [],
            links: []
        };
    }
};

const EntityVisualization = connect<LocalProps, any, any>(mapStateToProps, null)(EntityVisualizationComponent);

export default EntityVisualization;