<?php
$dir = './sample';
$tree = getdirtree($dir);
showdirtree($tree);

function getdirtree ( $path )
{ 
    if (!is_dir($path)) {
        return false;
    }

    $dir = array(); 

    if ($handle = opendir($path)) {
        while (false !== ($file = readdir($handle))) { 
            if ('.' == $file || '..' == $file) {
                continue;
            }
            if (is_dir($path.'/'.$file)) {
                $dir[$file] = getdirtree($path.'/'.$file);
            } elseif (is_file($path.'/'.$file) && mb_ereg("\.pen$", $file) ) {
                $dir[$file] = $path.'/'.$file;
            }
        }
        closedir($handle);
    }

    return $dir;
}

function showdirtree ( $tree )
{
    if (!is_array($tree)) { 
        return false;
    }
    foreach ($tree as $key => $value) {
        if (is_array($value)) { 
            print('+'. $key. "\n");
            showdirtree($value); 
        } elseif (preg_match('/\.pen$/i', $value)) {
            print('-'. $key. "\n");
        }
    }
    return true;
}

?>
