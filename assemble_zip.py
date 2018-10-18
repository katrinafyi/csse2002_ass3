#!/usr/bin/env python3.5

import os
import sys
import zipfile
import shutil
import subprocess
import json
import glob
import time

def do_zip_assemble(config: dict):
    TEMP_FOLDER = './__temp_'+str(int(time.time()*1000))

    print('Starting artifact build with config:')
    print(json.dumps(config, indent=4))
    
    src_files = config['src'] # type: dict

    print('Copying to temp directory', TEMP_FOLDER)
    shutil.copytree('.', TEMP_FOLDER, ignore=shutil.ignore_patterns(".git"))
    os.chdir(TEMP_FOLDER)

    if src_files.get('clean', True):
        print('Deleting spurious source files before test...')
        for f in os.listdir(src_files['src_path']):
            if f not in src_files['include']:
                print('    Deleted', f)
                os.unlink(src_files['src_path'] + '/' + f)
            else:
                print('    Kept', f)

        print('Executing tests...')
        subprocess.check_call(['mvn', 'clean', 'test', '-B'], shell=True)
    else:
        print('Skipped clean testing...')

    print('Compiling artifact zip...')

    if len(sys.argv) < 2:
        print('Requires zip name argument.')
        sys.exit(1)

    print('Writing zip file', sys.argv[1])

    zf = zipfile.ZipFile('./../'+sys.argv[1], 'w')

    for key, file_structure in config.items():
        print('  Handling config key:', key)
        src = file_structure['src_path']
        dst = file_structure['dest_path']
        sep = os.sep
        for f in file_structure['include']:
            if not file_structure.get('glob', False):
                print('    Adding', src+sep+f)
                zf.write(src+sep+f, dst+sep+f)
            else:
                print('    Adding files matching', src+sep+f)
                for f2 in glob.glob(os.path.join(src, f), recursive=True):
                    new = os.path.normpath(f2.replace(src, dst, 1))
                    print('     Adding', f2, 'as', new)
                    zf.write(f2, new)
    print('Removing temp directory...')
    os.chdir('..')
    shutil.rmtree(TEMP_FOLDER)
    print('Done.')


def main():
    with open('zip_structure.json') as f:
        do_zip_assemble(json.load(f))


if __name__ == '__main__':
    main()