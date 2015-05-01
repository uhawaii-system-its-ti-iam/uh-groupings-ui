'use strict';

var gulp = require('gulp');
var guppy = require('git-guppy')(gulp);
var $ = require('gulp-load-plugins')();
var stylish = require('jshint-stylish');
var watchify = require('watchify');
var browserify = require('browserify');
var source = require('vinyl-source-stream');
var buffer = require('vinyl-buffer');
var assign = require('lodash/object/assign');
var del = require('del');
var browserSync = require('browser-sync');
var reload = browserSync.reload;

var jsSources = ['*.js', 'app/**/*.js', '!**/bundle.js'];
var browserifyOpts = assign({}, watchify.args, {
  entries: ['./app/index.js'],
  debug: true
});
var w = watchify(browserify(browserifyOpts));
var b = browserify(browserifyOpts);

function bundle(mode) {
  return function () {
    return mode.bundle()
      // log errors if they happen
      .on('error', $.util.log.bind($.util, 'Browserify Error'))
      .pipe(source('bundle.js'))
      // optional, remove if you don't need to buffer file contents
      .pipe(buffer())
      // optional, remove if you dont want sourcemaps
      .pipe($.sourcemaps.init({ loadMaps: true })) // loads map from browserify file
      // Add transformation tasks to the pipeline here.
      .pipe($.sourcemaps.write('./')) // writes .map file
      .pipe(gulp.dest('dist/scripts'));
  };
}

w.on('update', bundle(w)); // on any dep update, runs the bundler
w.on('log', $.util.log); // output build logs to terminal
b.on('log', $.util.log); // output build logs to terminal

gulp.task('default', $.taskListing.withFilters(null, /pre-commit/));

// style/lint

gulp.task('jshint', ['jscs'], function () {
  return gulp.src(jsSources)
    .pipe($.jshint())
    .pipe($.jshint.reporter(stylish))
    .pipe($.if(!browserSync.active, $.jshint.reporter('fail')));
});

gulp.task('jshint:test', function () {
  return gulp.src('test/*.js')
    .pipe($.jshint())
    .pipe($.jshint.reporter(stylish))
    .pipe($.if(!browserSync.active, $.jshint.reporter('fail')));
});

gulp.task('jshint:all', ['jshint', 'jshint:test']);

gulp.task('jscs', function () {
  return gulp.src(jsSources)
    .pipe($.jscs());
});

// build/watch

gulp.task('js', bundle(w));
gulp.task('js:build', bundle(b));

gulp.task('styles', function () {
  return gulp.src('sass/**/*.scss')
    .pipe($.sourcemaps.init())
    .pipe($.sass({
      outputStyle: 'nested', // libsass doesn't support expanded yet
      precision: 10,
      includePaths: ['.'],
      onError: console.error.bind(console, 'Sass error:')
    }))
    .pipe($.postcss([
      require('autoprefixer-core')({ browsers: ['last 1 version'] })
    ]))
    .pipe($.sourcemaps.write())
    .pipe(gulp.dest('.tmp/styles'))
    .pipe(reload({ stream: true }));
});

gulp.task('html', ['styles'], function () {
  var assets = $.useref.assets({ searchPath: ['.tmp', 'public'] });

  return gulp.src('public/*.html')
    .pipe(assets)
    .pipe($.if('*.js', $.uglify()))
    .pipe($.if('*.css', $.csso()))
    .pipe(assets.restore())
    .pipe($.useref())
    .pipe($.if('*.html', $.minifyHtml({
      conditionals: true,
      loose: true
    })))
    .pipe(gulp.dest('dist'));
});

gulp.task('images', function () {
  return gulp.src('public/images/**/*')
    .pipe($.cache($.imagemin({
      progressive: true,
      interlaced: true,
      // don't remove IDs from SVGs, they are often used
      // as hooks for embedding and styling
      svgoPlugins: [{ cleanupIDs: false }]
    })))
    .pipe(gulp.dest('dist/images'));
});

gulp.task('fonts', function () {
  return gulp.src(require('main-bower-files')({
    filter: '**/*.{eot,svg,ttf,woff,woff2}'
  }).concat('public/fonts/**/*'))
    .pipe(gulp.dest('.tmp/fonts'))
    .pipe(gulp.dest('dist/fonts'));
});

gulp.task('extras', function () {
  return gulp.src(['public/*.*', '!public/*.html'
  ], { dot: true })
    .pipe(gulp.dest('dist'));
});

gulp.task('clean', del.bind(null, ['.tmp', 'dist']));

gulp.task('serve', ['js', 'styles', 'fonts'], function () {
  browserSync({
    notify: false,
    port: 9000,
    server: {
      baseDir: ['.tmp', 'public', 'dist/scripts'],
      routes: {
        '/bower_components': 'bower_components'
      }
    }
  });

  // watch for changes
  gulp.watch([
    'public/*.html',
    'dist/scripts/bundle.js',
    'public/images/**/*',
    '.tmp/fonts/**/*'
  ]).on('change', reload);

  gulp.watch('sass/**/*.scss', ['styles']);
  gulp.watch('public/fonts/**/*', ['fonts']);
  gulp.watch('bower.json', ['wiredep', 'fonts']);
});

gulp.task('serve:dist', function () {
  browserSync({
    notify: false,
    port: 9000,
    server: {
      baseDir: ['dist']
    }
  });
});

gulp.task('serve:test', function () {
  browserSync({
    notify: false,
    open: false,
    port: 9000,
    ui: false,
    server: {
      baseDir: 'test'
    }
  });

  gulp.watch([
    'test/*.js'
  ]).on('change', reload);

  gulp.watch('test/*.js', ['jshint:test']);
});

gulp.task('wiredep', function () {
  var wiredep = require('wiredep').stream;

  gulp.src('sass/*.scss')
    .pipe(wiredep({
      ignorePath: /^(\.\.\/)+/
    }))
    .pipe(gulp.dest('sass/styles'));

  gulp.src('public/*.html')
    .pipe(wiredep({
      ignorePath: /^(\.\.\/)*\.\./
    }))
    .pipe(gulp.dest('public'));
});

gulp.task('build:clean', ['clean'], function () {
  gulp.start('build');
});

gulp.task('build', ['jshint', 'js:build', 'html', 'images', 'fonts', 'extras'], function () {
  return gulp.src('dist/**/*')
    .pipe($.size({
      title: 'build',
      gzip: true
    }));
});

// git hooks

gulp.task('pre-commit', ['jshint:all'], function () {
  return guppy.stream('pre-commit')
    .pipe($.filter(['*.js']))
    .pipe($.jshint())
    .pipe($.jshint.reporter(stylish))
    .pipe($.jshint.reporter('fail'));
});
