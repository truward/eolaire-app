var React = require('React');
var Loading = require('../common/loading.js');
var ItemsTable = require('./items-table.js');

module.exports = React.createClass({
  getInitialState: function() {
    return {loading: true};
  },

  componentDidMount: function() {
    this._fetch(this.props.type, this.props.offsetToken, this.props.limit);
  },

  componentWillReceiveProps: function(nextProps) {
    this._fetch(nextProps.type, nextProps.offsetToken, nextProps.limit);
  },

  render: function() {
    if (this.state.loading) {
      return (<Loading target="Items"/>);
    }

    return (
      <div className="container">
        <h2>Items</h2>
        <ItemsTable items={this.state.items} />
      </div>
    );
  },

  //
  // Private
  //

  _fetch: function (type, offsetToken, limit) {
    var promise = this.props.services.eolaireService.getItemListByType(type, offsetToken, limit);

    promise.then(function (response) {
      this.props.services.titleService.setTitle("Item List");
      this.setState({items: response["items"], loading: false});
    }.bind(this));
  }
});

