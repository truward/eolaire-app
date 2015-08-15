var React = require('React');

var DEFAULT_LIMIT = require('../../util/constants.js').DEFAULT_LIMIT;

module.exports = React.createClass({
  render: function() {
    var rows = this.props.items.map(function (item) {
      //var href = "#/item/type/" + item["id"] + "/limit/" + DEFAULT_LIMIT;
      return (
        <tr key={item["id"]}>
          <td>{item["id"]}</td>
          <td>{item["itemTypeId"]}</td>
          <td>{item["name"]}</td>
          <td>TBD</td>
        </tr>
      );
    }.bind(this));

    return (
      <table className="table">
        <thead>
          <th>ID</th>
          <th>Type</th>
          <th>Name</th>
          <th>Related Items</th>
        </thead>
        <tbody>{rows}</tbody>
      </table>
    );
  }
});

