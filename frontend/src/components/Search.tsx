import React from "react"
import {EntityType} from "../logic/types";
import * as action from "../logic/action";
import {connect} from "react-redux";
import {Dispatch} from "redux";
import * as Rx from 'rxjs';
import * as operators from 'rxjs/operators';

const mapDispatchToProps = (dispatch: Dispatch) => ({
    filterEntity(namePattern: string | null, entityType: EntityType | null) {
        dispatch(action.actionCalls.filterEntities(namePattern, entityType))
    }
});

type LocalProps = ReturnType<typeof mapDispatchToProps>

class SearchComponent extends React.Component<LocalProps> {
    private inputRef: React.RefObject<HTMLInputElement> = React.createRef();
    private selectRef: React.RefObject<HTMLSelectElement> = React.createRef();

    componentDidMount(): void {
        // @ts-ignore
        const patternObservable = Rx.fromEvent(this.inputRef.current, "input");
        // @ts-ignore
        const typeObservable = Rx.fromEvent(this.selectRef.current, "input");
        Rx.merge(patternObservable, typeObservable).pipe(
            operators.debounce(event => Rx.interval(1000))
        ).subscribe(event => {
            // @ts-ignore
            const namePattern = this.inputRef.current.value;
            // @ts-ignore
            const entityType = this.selectedValueToEntityType(this.selectRef.current.value);

            this.props.filterEntity(namePattern, entityType)
        });
    }

    selectedValueToEntityType(value: string): EntityType | null {
        switch (value) {
            case "2": return "Service";
            case "3": return "Storage";
            case "4": return "MessageQueue";
            default: return null;
        }
    }

    render() {
        return (
            <div>
                <div className="input-group mb-3">
                    <div className="input-group-prepend">
                        <span className="input-group-text" id="basic-addon1">Search</span>
                    </div>
                    <input type="text" className="form-control" placeholder="Subject name" aria-label="subject" ref={this.inputRef}
                           aria-describedby="basic-addon1"/>
                </div>

                <div className="input-group mb-3">
                    <div className="input-group-prepend">
                        <label className="input-group-text" htmlFor="inputGroupSelect01">Type</label>
                    </div>
                    <select className="custom-select" id="inputGroupSelect01" defaultValue="1" ref={this.selectRef}>
                        <option value="1">All</option>
                        <option value="2">Service</option>
                        <option value="3">Storage</option>
                        <option value="4">MessageQueue</option>
                    </select>
                </div>
            </div>
        );
    }
}

const Search = connect<any, any, any>(null, mapDispatchToProps)(SearchComponent);

export default Search;
