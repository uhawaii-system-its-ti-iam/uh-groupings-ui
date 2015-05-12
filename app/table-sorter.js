'use strict';

var $ = require('jquery');

module.exports = function (e) {
  var $el = $(e.currentTarget);
  var $sortIcon = $el.find('span.fa');
  var propertyName = $el.data('property');
  var sortOrder = $el.data('sort');
  var collection = this.collection;
  var fullCollection = collection.fullCollection;

  var sortFlip = {
    asc: { name: 'desc', value: 1 },
    desc: { name: 'asc', value: -1 }
  };

  if (propertyName && sortOrder) {
    var isVisible = $sortIcon.is(':visible');
    var newOrder = isVisible ? sortFlip[sortOrder].name : sortOrder;
    var numOrder = isVisible ? sortFlip[sortOrder].value :
      // get numeric value for current order
      sortFlip[sortFlip[sortOrder].name].value;

    $sortIcon.removeClass('hidden');

    // update data value for future sorts
    $el.data('sort', newOrder);

    // update sort icon
    if (isVisible) {
      $sortIcon.toggleClass('fa-sort-' + sortOrder);
      $sortIcon.toggleClass('fa-sort-' + newOrder);
    }

    // hide sort icon for columns not sorted by
    $el.siblings('th').find('span.fa').toggleClass('hidden', true);

    // set sort key and order
    collection.setSorting(propertyName, numOrder);

    // trigger sort
    fullCollection.sort();
  }
};
