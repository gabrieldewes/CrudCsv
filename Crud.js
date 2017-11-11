(function() {
  /**
   * Created by Dewes on 09/11/2017.
   */
  "use strict";

  var fs = require("fs");
  var readLine = require("readline");
  var stream = require("stream");

  const DATA_PATH = "data/";
  const USERDATA = DATA_PATH + "userdata.csv";

  function init() {
    if ( !fs.existsSync(DATA_PATH)) {
      fs.mkdirSync(DATA_PATH);
      console.log("[init] Created path =", DATA_PATH);
      write("id,name");
    }
  }

  function bufferedList(callback) {
    var lines = [];
    var input = fs.createReadStream(USERDATA);
    var readLineInterface = readLine.createInterface(input, new stream);
    readLineInterface.on("line", (line) => {
      lines.push(line.split(","));
    });
    readLineInterface.on("close", () => {
      callback(lines);
    });
  }

  function listAll() {
    var list = [];
    var lines = fs.readFileSync(USERDATA, 'utf8').split("\r\n");
    delete lines[lines.length - 1];
    lines.forEach( (line) => {
      list.push(line.split(","))
    })
    return list;
  }

  function write(content) {
    fs.appendFileSync(USERDATA, content + "\r\n");
    console.log("[write] Writing line = " + content)
  }

  function replaceWith(id, content) {
    var lines = fs.readFileSync(USERDATA, 'utf8').split("\r\n");
    delete lines[lines.length - 1];
    var found = false;
    for (var i in lines) {
      var old = lines[i].split(",");
      if (old[0] !== "id" && Number(old[0]) === Number(id)) {
        found = true;
        lines[i] = old[0] + "," + content;
        fs.writeFileSync(USERDATA, lines.join("\r\n"));
        console.log("[replaceWith] Content changed from '" + old[1] + "' to '" + content + "'");
      }
    }
    if ( !found) {
      console.warn("[replaceWith] Line not found with id =", id);
    }
  }

  function remove(id) {
    var lines = fs.readFileSync(USERDATA, 'utf8').split("\r\n");
    delete lines[lines.length - 1];
    var found = false;
    for (var i in lines) {
      var old = lines[i].split(",");
      if (old[0] !== "id" && Number(old[0]) === Number(id)) {
        found = true;
        var index = lines.indexOf(lines[i]);
        if (index > -1) {
          lines.splice(index, 1);
        }
        fs.writeFileSync(USERDATA, lines.join("\r\n"));
        console.log("[remove] Removed line =", old)
      }
    }
    if ( !found) {
      console.warn("[remove] Line not found with id =", id);
    }
  }

  function bubbleSort(lines) {
    for (var i=lines.length; i>1; i--) {
        for (var j=2; j<i; j++) {
            if (Number(lines[j - 1][0]) < Number(lines[j][0])) {
                var aux = lines[j];
                lines[j] = lines[j - 1];
                lines[j - 1] = aux;
            }
        }
    }
  }

  init();

  write("0,Gabriel");
  write("1,Fernando");
  write("2,Juliana");

  var list = listAll();
  list.forEach( (line) => {
    console.log("[main]", line)
  });

  bubbleSort(list);

  list.forEach( (line) => {
    console.log("[main] [bubbleSort desc]", line)
  });

  replaceWith(1, "Dewes");
  replaceWith(0, "Dewes");
  replaceWith(2, "Dewes");
  replaceWith(3, "Err");

  listAll().forEach( (line) => {
    console.log("[main]", line)
  });

  remove(0);
  remove(1);
  remove(2);
  remove(3);

})()
