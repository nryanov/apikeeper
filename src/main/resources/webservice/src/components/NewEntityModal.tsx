import React from 'react'
import {connect} from 'react-redux'
import {EntityType} from "../logic/types";
import * as action from "../logic/action";

const mapDispatchToProps = dispatch => ({
    createEntity(name: string, entityType: EntityType, description: string | null) {
        dispatch(action.apiCalls.createEntity(({
            name,
            entityType,
            description
        })));
    }
});

type LocalProps = ReturnType<typeof mapDispatchToProps>

class NewEntityModalComponent extends React.Component<LocalProps> {
    private entityNameInput: React.RefObject<HTMLInputElement> = React.createRef();
    private entityTypeInput: React.RefObject<HTMLSelectElement> = React.createRef();
    private entityDescriptionInput: React.RefObject<HTMLTextAreaElement> = React.createRef();
    private closeButton: React.RefObject<HTMLButtonElement> = React.createRef();

    constructor(props) {
        super(props);

        this.clearInputs = this.clearInputs.bind(this);
        this.newEntity = this.newEntity.bind(this);
    }

    clearInputs() {
        // @ts-ignore
        this.entityNameInput.current.value = "";
        // @ts-ignore
        this.entityTypeInput.current.value = "Service";
        // @ts-ignore
        this.entityDescriptionInput.current.value = "";
    }

    newEntity() {
        // @ts-ignore
        const entityName = this.entityNameInput.current.value;
        // @ts-ignore
        const entityType = this.entityTypeInput.current.value as EntityType;
        // @ts-ignore
        const entityDescription = this.entityDescriptionInput.current.value;

        if (entityName.length > 0) {

            this.props.createEntity(entityName, entityType, entityDescription);
            this.clearInputs();
            // @ts-ignore
            this.closeButton.current.click();
        } else {
            // @ts-ignore
            this.entityNameInput.current.classList.add('is-invalid');
        }
    }

    render() {
        return (
            <div className="modal fade" id="newSubjectModal" tabIndex={-1} role="dialog"
                 aria-labelledby="newSubjectModalLabel"
                 aria-hidden="true">
                <div className="modal-dialog" role="document">
                    <div className="modal-content">
                        <div className="modal-header">
                            <h5 className="modal-title" id="newSubjectModalLabel">New subject</h5>
                            <button type="button" className="close" data-dismiss="modal"
                                    aria-label="Close">
                                <span aria-hidden="true">&times;</span>
                            </button>
                        </div>
                        <div className="modal-body">
                            <form>
                                <div className="form-group">
                                    <label htmlFor="newSubjectFormControlName">Entity name</label>
                                    <input type="text" className="form-control" ref={this.entityNameInput}
                                           id="newEntityName"
                                           placeholder="Entity name"
                                    />
                                    <div className="invalid-feedback">
                                        Entity name should not be empty
                                    </div>
                                </div>
                                <div className="form-group">
                                    <label htmlFor="newSubjectFormControlCompatibilityType">Entity type</label>
                                    <select className="form-control" ref={this.entityTypeInput}
                                            id="newEntityType">
                                        <option>Service</option>
                                        <option>Storage</option>
                                        <option>MessageQueue</option>
                                    </select>
                                </div>
                                <div className="form-group">
                                    <label htmlFor="newSubjectFormControlSchema">Schema (optional)</label>
                                    <textarea className='form-control' id="newSubjectFormControlSchema"
                                              ref={this.entityDescriptionInput} rows={3}>
                                    </textarea>
                                </div>
                            </form>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-secondary" data-dismiss="modal" ref={this.closeButton}>Close</button>
                            <button type="button" className="btn btn-primary" onClick={this.newEntity}>Save changes</button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

const NewEntityModal = connect(null, mapDispatchToProps)(NewEntityModalComponent);

export default NewEntityModal;