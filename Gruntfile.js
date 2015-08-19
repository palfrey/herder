'use strict';
module.exports = function(grunt) {
    grunt.initConfig({
        prettify: {
            all: {
                expand: true,
                src: ['resources/**/*.mustache'],
            }
        },
        jsbeautifier: {
            files: ["Gruntfile.js"],
        },
        watch: {
            html: {
                files: ['**/*.mustache'],
                tasks: ['prettify'],
            },
            js: {
                files: ['Gruntfile.js'],
                tasks: ['jsbeautifier'],
            },
        },

    });

    grunt.loadNpmTasks('grunt-prettify');
    grunt.loadNpmTasks('grunt-contrib-watch');
    grunt.loadNpmTasks("grunt-jsbeautifier");

    grunt.registerTask('default', ['prettify', 'jsbeautifier', 'watch']);

    grunt.event.on('watch', function(action, filepath) {
        grunt.config('prettify.all.src', filepath);
    });
};
