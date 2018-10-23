from collections import defaultdict


WIDTH = 20
DIRECTIONS = {
    'north': (0, -1),
    'east': (1, 0),
    'south': (0, 1),
    'west': (-1, 0)
}

def pos_to_n(x, y):
    return WIDTH*y + x

def main():
    exits = defaultdict(lambda: dict())


    with open('maps/test.map', 'w') as f:
        f.write('0\n0\nTest Name\n')
        f.write(','.join(('wood,soil', )*200))
        f.write(f'\n\ntotal:{WIDTH**2}\n')

        i = 0
        for y in range(WIDTH):
            for x in range(WIDTH):
                f.write(str(i) + ' soil,grass\n')
                for d, shift in DIRECTIONS.items():
                    if 0 <= x+shift[0] < WIDTH and 0 <= y+shift[1] < WIDTH:
                        exits[i][d] = pos_to_n(x+shift[0], y+shift[1])
                i += 1
        f.write('\nexits\n')
        for i, e in exits.items():
            f.write(str(i) + ' ' + ','.join(f'{a}:{b}' for a, b in e.items())+'\n')


    

if __name__ == '__main__':
    main()