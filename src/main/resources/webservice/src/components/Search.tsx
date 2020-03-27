import React from "react"

class Search extends React.Component {
    render() {
        return (
            <div>
                <div className="input-group mb-3">
                    <div className="input-group-prepend">
                        <span className="input-group-text" id="basic-addon1">Search</span>
                    </div>
                    <input type="text" className="form-control" placeholder="Subject name" aria-label="subject"
                           aria-describedby="basic-addon1"/>
                </div>

                <div className="input-group mb-3">
                    <div className="input-group-prepend">
                        <label className="input-group-text" htmlFor="inputGroupSelect01">Type</label>
                    </div>
                    <select className="custom-select" id="inputGroupSelect01">
                        <option selected>Choose type...</option>
                        <option value="1">Service</option>
                        <option value="2">Storage</option>
                        <option value="3">MessageQueue</option>
                    </select>
                </div>
            </div>
        );
    }
}

export default Search;