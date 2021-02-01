Monitor
=======

A simple tool to make HTTP requests every minute and compare the response status codes to expected values, sending an email in the event of unexpected values.

Config
------

See `src/main/resources/application.yml` for a full example

**Monitored**

The following YAML defines URLs to test, expected responses and email addresses to notify.

```yaml
monitored:
  checks:
    - url: http://example.com
      method: HEAD
      expectedStatus: OK
      recipients: to@example.com
    - url: http://www.example.com
      method: GET
      expectedStatus: OK
      recipients: to@example.com
    - url: http://localhost/foo
      method: POST
      expectedStatus: OK
      recipients:
        - to@example.com
```

**Digest**

As a check that the application is still running, it sends a digest email daily, containing a count of checks performed since the last digest email, configured with:

```yaml
digest:
  recipients: digest@example.com
  ```