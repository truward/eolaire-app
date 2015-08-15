var React = require('React');
var Loading = require('../common/loading.js');
var EntitiesTable = require('./entities-table.js');

module.exports = React.createClass({
  getInitialState: function() {
    return {loading: true};
  },

  componentDidMount: function() {
    this._fetch(this.props.offsetToken, this.props.limit);
  },

  componentWillReceiveProps: function(nextProps) {
    this._fetch(nextProps.offsetToken, nextProps.limit);
  },

  render: function() {
    if (this.state.loading) {
      return (<Loading target="Entities"/>);
    }

    return (
      <div className="container">
        <h2>Entity Items</h2>
        <EntitiesTable items={this.state.items} />
      </div>
    );
  },

  //
  // Private
  //

  _fetch: function (offsetToken, limit) {
    var promise = this.props.services.eolaireService.getAllEntities(offsetToken, limit);

    promise.then(function (response) {
      this.props.services.titleService.setTitle("Entity List");
      this.setState({items: response["types"], loading: false});
    }.bind(this));
  }
});

