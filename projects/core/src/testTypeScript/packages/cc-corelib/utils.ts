export function withTermColor<T>(color: number, func: () => T): T {
    const currentColor = term.getTextColor();
    term.setTextColor(color);
    const result = func();
    term.setTextColor(currentColor);
    return result;
}

export function calculateLength(table: LuaTable<any, any>): number {
    let counter = 0;
    for (const [_key, _value] of table) counter += 1;
    return counter;
}
