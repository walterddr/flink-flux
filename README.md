# Streaming plugins for Apache Flink @Uber

[![Build Status][ci-img]][ci] [![ReadTheDocs][doc-img]][doc]

This is a repository contains all Apache Flink plugin libraries developed by
the streaming analytics team @Uber.

## Streaming plugins for Apache Flink
`streaming-plugins` is a plugin library used for various purposes including:
Wrapped executor for interacting with deployment clusters; generalized wrapper
for validating and explaining Flink job graphs by pulling in external upstream,
downstream dependencies. etc.

This is especially useful for managing enormous amount of Flink applications
together within a service-oriented architecture.

## Streaming DSL for Apache Flink
`streaming-dsl` is a streaming DSL designed specifically for constructing 
Apache Flink applications through markup languages.

[doc-img]: https://readthedocs.org/projects/uber-flink-plugins/badge/?version=latest
[doc]: http://uber-flink-plugins.readthedocs.org/en/latest/
[ci-img]: https://api.travis-ci.com/uber/flink-plugins.svg?branch=master 
[ci]: https://travis-ci.com/uber/flink-plugins

