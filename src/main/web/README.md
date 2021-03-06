# Overview

This is the Workbench web development source folder. Web development differs from Java development in that we often want quick redeployments to the browser to test our changes. We also want use of pre-processing tools such as Sass. The tools and code structure here allow for both of these.

# Source Structure

Code is built from the source folder (this folder) to `../webapp/WEB-INF`. 

Do **NOT** edit files under `/WEB-INF/pages` or `/WEB-INF/static` directly. These will be overwritten by their source files in this folder whenever a build is run. All files should be committed to the source folder (`../src/main/web`) only.

# Building

## Node

You will need to have Node.js [v0.12.7](https://nodejs.org/dist/v0.12.7/) installed. If you are running on Windows, this will need to be the 64 bit version of Node due to bitness issues with node-sass running Node locally vs from the frontend-maven-plugin.

## Maven

When an ordinary Maven build (`mvn clean install`) is run we use the maven-node-gulp plugin to install Node.js and npm. Then it runs a gulp clean and build. The clean will delete the `/WEB-INF/pages` and `/WEB-INF/static` folders and will rebuild everything from scratch. The WEB-INF folder will then be included in the war file that can be deployed to Tomcat.

## Gulp

To use the gulp tasks directly, you will need to run a `mvn clean install` in the top level of Workbench. This will run `npm install` as part of the task and install the Node modules required for Workbench front end development.

You can then run the gulp build task directly with `gulp build`. There are also a number of other tasks that you can run manually (that will be included in a build):

* `clean` - removes `/WEB-INF/static` and `/WEB-INF/pages`
* `build` - runs `'js'`, `'sass'`, `'images'`, `'html'`, `'fonts'`, `'angular'` and `'resources'`. Running with the `--release` flag will minify JS and CSS.
* `fonts` - copies font files to the build directory
* `html` - copies the contents of the src pages folder to the build folder
* `images` - compresses the images and copies them to the built images folder
* `resources` - copies the files from the resources folder to the build directory
* `js` - will JSHint any non-library code, and copy all JavaScript files (libraries too) to the build folder. Running with the `--release` flag will minify files.
* `angular` - concatenates all Angular JS and copies it to the build folder. Running with the `--release` flag will minify files. It also compiles the scss files related to angular components, adds vendor prefixes, converts rem to px and copies to the build folder. Running with the `--release` flag will minify files.
* `sass` - compiles scss files into css, adds vendor prefixes and copies to the build folder. Running with the `--release` flag will minify files.
* `test` - runs the JavaScript tests
* `watch` - will invoke the appropriate task when files in the `src` folder change (note that the watch task will need restarting to pick up newly created files)

Running `gulp` without a specified task will run the `build` task.

__Note:__ If you see the following error when running `gulp watch`: 
`Error: 'libsass' bindings not found.` then as a workaround, delete the existing `node_modules` folder and re-run `npm install`. If this doesn't help, the issue could be due to having a version of Node installed that is lower than the required v0.12.7.

## Building for Production

We must build our files for production with the `--release` flag. This will minify JavaScript and CSS files. The `--release` flag will be applied when doing a Maven build with the `release` environment config, or you can run `gulp build --release`.

# Development

When developing in a web environment, it is useful to not have to re-build and deploy war files every time you want to test a change. We have added tools to enable this. To enable this, call:

`gulp watch --env=<property>`

where `<property>` is a property defined in the `gulp.properties` file specifying the location of your Tomcat installation. This will watch your source folder for changes, and as you develop, will trigger the correct tasks and copy files over the build folder as necessary. In addition to this, **changed files will be copied directly into the exploded Workbench folder iny our Tomcat webapps directory**, allowing you to refresh your browser and see changes without redeploying.

In addtion to this, there are a few things to note about developing in this environment:

* We use Sass, not CSS, which you can find in the sass folder
* We use the auto-prefixer plugin to automatically add vendor prefixes to our CSS. This happens at build time, so we don't have to specify them ourselves in our Sass files. For more information and to check if your rule is supported, see the [website](https://github.com/postcss/autoprefixer-core)
* We use an image minification library to compress our images on build
* JS libraries should be included already minified in the `js/lib` folder

## Potential issues

* During development if you are introducing a new module, due to the inclusion of the Strict DI Mode directive (`ng-strict-di`), you may see an error that the ontology module cannot be instantiated. The cause of this issue will be that the DI for the module that you are adding is not correct - there is likely a module dependency missing that needs to be included. When this issue occurs, it is caught in the build process by JSHint, which causes the built `ontology.js` file to not be created correctly. It will not include all of the JS concatenated together as it should. To resolve this, you will need to fix up your DI in your module, and then run `gulp clean` before building again. You will also need to ensure that `ontology.js` has the correct content when loaded in your browser.

# Test Coverage Report

To see a report of the JS unit test coverage, run `gulp test --ci`.
The coverage report can be found in `reports/coverage/lcov-report/index.html`.

# Libraries

## Angular
When upgrading the Angular version, the `angular-mocks.js` library also needs to be upgraded to the same version in order for the tests to run.

Note that the minified Angular library has a complimentary source map (`.map`) file to assist in debugging. This is excluded from being copied to the `WEB-INF\static` directory. 

## Others

We use a couple of third party libraries. Any customisations performed to third party libraries should be listed here:

* UI Bootstrap - Custom build of UI Bootstrap containing the following modules:
    - Dateparser
    - Position
    - Datepicker
