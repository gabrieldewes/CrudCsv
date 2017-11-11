<?php
/**
 * Created by Dewes on 09/11/2017.
 */
class Crud {
  const DATA_PATH = "data/";
  const USERDATA = self::DATA_PATH . "userdata.csv";

  function __construct() {
    self::init();
  }

  function init() {
    $path = self::DATA_PATH;
    if ( !is_dir($path) && mkdir($path)) {
      self::debug("[init] Created dir = " . $path);
      self::write("id,name");
    }
  }

  function listAll() {
    $handle = fopen(self::USERDATA, "r");
    $list = [];
    if ($handle) {
      while (($line = fgets($handle)) !== false) {
        array_push($list, explode(",", str_replace(PHP_EOL, "", $line)));
      }
      fclose($handle);
    } else {
      self::debug("[listAll] Error opening file = " . self::USERDATA);
    }
    return $list;
  }

  function write($content) {
    $path = self::USERDATA;
    $file = fopen($path, "a");
    if (!$file) {
      self::debug("[write] Error while opening file = " . $path);
      exit;
    }
    $content = $content . PHP_EOL;
    $res = fwrite($file, $content);
    if (!$res) {
      self::debug("[write] Error while writing file");
      exit;
    }
    self::debug("[write] Writing line = " . $content);
    fclose($file);

  }

  function replaceWith($id, $content) {
    $path = self::USERDATA;
    $file = file($path);
    $out = [];
    $found = false;
    foreach($file as $line) {
      $old = explode(",", str_replace(PHP_EOL, "", $line));
      if ($old[0] != "id" && $old[0] == $id) {
        $found = true;
        $line = $old[0] . "," . $content . PHP_EOL;
        self::debug("[replaceWith] Content changed from '" . $old[1] . "' to '" . $content ."'");
      }
      $out[] = $line;
    }
    $fp = fopen($path, "w+");
    flock($fp, LOCK_EX);
    foreach($out as $line) {
      fwrite($fp, $line);
    }
    flock($fp, LOCK_UN);
    fclose($fp);
    if ( !$found) {
      self::debug("[replaceWith] Line not found with id = " . $id);
    }
  }

  function remove($id) {
    $path = self::USERDATA;
    $file = file($path);
    $out = [];
    $found = false;
    foreach($file as $line) {
      $old = explode(",", str_replace(PHP_EOL, "", $line));
      if ($old[0] != "id" && $old[0] == $id) {
        $found = true;
        self::debug("[remove] Removed line = " . json_encode($old));
      } else {
        $out[] = $line;
      }
    }
    $fp = fopen($path, "w+");
    flock($fp, LOCK_EX);
    foreach($out as $line) {
      fwrite($fp, $line);
    }
    flock($fp, LOCK_UN);
    fclose($fp);
    if ( !$found) {
      self::debug("[remove] Line not found with id = " . $id);
    }
  }

  function bubbleSort($list) {
    for ($i=count($list); $i>1; $i--) {
      for ($j=2; $j<$i; $j++) {
        if ( ((int) $list[$j - 1][0]) < ((int) $list[$j][0])) {
          $aux = $list[$j];
          $list[$j] = $list[$j - 1];
          $list[$j - 1] = $aux;
        }
      }
    }
    return $list;
  }

  function debug($str) {
    echo $str . PHP_EOL;
  }

}

$c = new Crud();

$c->write("0,Fernando");
$c->write("1,Maria");
$c->write("2,Oliveira");

$c->replaceWith(2, "Dewes");
$c->replaceWith(0, "Dewes");
$c->replaceWith(1, "Dewes");
$c->replaceWith(3, "Err");

$list = $c->listAll();

foreach ($list as $line) {
  $c->debug("[main] " . json_encode($line));
}

$list = $c->bubbleSort($list);

foreach ($list as $line) {
  $c->debug("[main] [bubbleSort desc] " . json_encode($line));
}

$c->remove(1);
$c->remove(0);
$c->remove(2);
$c->remove(3);
?>
