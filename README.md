## DFormatter ##

A plugin to convert scripts written in bracket syntax to colon syntax. By having the plugin installed, files in the DFormatter folder are automatically 
updated and pasted to the output destination. With async procession and command calls on complete, it provides a near seamless transition.

### Bracket syntax ###
The most basic forms are converted. Check out `test/resources/` for examples. <br>
Also some shortcuts like `<pl>` or `<c.args>` are replaced to their full form.

## Command ##

**`/fm reload (simple) (silent)`**

The command can be used ingame or from the console, with 2 optional arguments.

* simple skips the following command execution.
* silent skips the message, when complete.

## Script reloading
A custom script can be used for script reloading, when the ``/fm`` command completes. <br>
This gives a nice transition of the process and also shows, when errors are found. 
```yml
MyReloadCommand:
  type: command
  permission: reload.admin
  name: reloadScripts
  script:
  - if <context.server> {
    - flag server meta.reload:server
    } else {
    - flag server meta.reload:<pl>
    }
  - reload

MyReloadWorld:
  type: world
  events:
    on reload scripts:
    - define target <server.flag[meta.reload]>
    - if <[target]> == server {
      - inject locally message
      } else {
      - inject locally message player:<[target]>
      }
  message:
  - if <context.had_error> {
    - narrate "<&a>Scripts reloaded. <&c>(ERROR FOUND)"
    } else {
    - narrate "<&a>Scripts reloaded."
    }
```
