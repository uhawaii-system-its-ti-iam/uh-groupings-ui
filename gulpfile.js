'use strict';

var gulp = require('gulp');
var guppy = require('git-guppy')(gulp);
var $ = require('gulp-load-plugins')();
var stylish = require('jshint-stylish');

var jsSources = ['*.js', 'app/**/*.js', '!app/**/vendor/**/*.js', '!Gruntfile.js'];

gulp.task('lint', ['jscs'], function () {
  return gulp.src(jsSources)
    .pipe($.jshint())
    .pipe($.jshint.reporter(stylish))
    .pipe($.jshint.reporter('fail'));
});

gulp.task('jscs', function () {
  return gulp.src(jsSources)
    .pipe($.jscs());
});

gulp.task('pre-commit', ['lint'], function () {
  return guppy.stream('pre-commit')
    .pipe($.filter(['*.js']))
    .pipe($.jshint())
    .pipe($.jshint.reporter(stylish))
    .pipe($.jshint.reporter('fail'));
});
