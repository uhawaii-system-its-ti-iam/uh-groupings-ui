/* global $ */
'use strict';

var Backbone = require('backbone');
var paginatorTmpl = require('../../templates/paginator.hbs');

function paginate(pageList, index) {
  // if there are at least 2 pages on each side of index
  if (index > 3 && index < pageList.length - 2) {
    return pageList.slice(index - 3, index + 2);
  // else if beginning of list
  } else if (index < 5) {
    return pageList.slice(0, 5);
  }

  // otherwise end of list
  return pageList.slice(-5);
}

module.exports = Backbone.Marionette.View.extend({
  template: paginatorTmpl,
  id: 'paginator-nav',

  render: function () {
    var _this = this;
    var $el = this.$el;
    var collection = this.collection;
    var state = collection.state;

    var viewData = {
      hasMoreThanSm: state.totalRecords > 49,
      hasMoreThanMd: state.totalRecords > 99,
      hasNext: collection.hasNextPage(),
      hasPrevious: collection.hasPreviousPage(),
      pages: [],
      sizeSm: state.pageSize === 20 || state.totalRecords < 20,
      sizeMd: state.pageSize === 50,
      sizeLg: state.pageSize === 100
    };

    var i = 0;
    while (i++ < state.totalPages) {
      // create page object for template
      var page = {
        isCurrent: state.currentPage === i,
        number: i
      };

      viewData.pages.push(page);
    }

    // slice pages based on current page index
    viewData.pages = paginate(viewData.pages, state.currentPage);

    // inject rendered paginator template
    $el.html(paginatorTmpl(viewData));

    // bind click events to paginator buttons

    var $pageBtns = $el.find('.btn-paginator');

    $pageBtns.on('click', function () {
      var pageNumber = +$(this).text();

      collection.getPage(pageNumber);

      // rerender paginator view
      _this.render();

      return false;
    });

    var $firstBtn = $el.find('#paginator-first');

    $firstBtn.on('click', function () {
      collection.getFirstPage();

      // rerender paginator view
      _this.render();

      return false;
    });

    var $prevBtn = $el.find('#paginator-prev');

    $prevBtn.on('click', function () {
      collection.getPreviousPage();

      // rerender paginator view
      _this.render();

      return false;
    });

    var $nextBtn = $el.find('#paginator-next');

    $nextBtn.on('click', function () {
      collection.getNextPage();

      // rerender paginator view
      _this.render();

      return false;
    });

    var $lastBtn = $el.find('#paginator-last');

    $lastBtn.on('click', function () {
      collection.getLastPage();

      // rerender paginator view
      _this.render();

      return false;
    });

    var $pageSizers = $el.find('#paginator-sizer button')
      .not(':disabled')
      .not('.active');

    $pageSizers.on('click', function () {
      var $el = $(this);

      $pageSizers.removeClass('active');
      $el.addClass('active');

      collection.setPageSize(+$el.text());

      // rerender paginator view
      _this.render();

      return false;
    });
  }
});
