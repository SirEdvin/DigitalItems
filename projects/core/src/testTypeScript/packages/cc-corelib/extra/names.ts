function buildNamer(modID: string, name: string): () => string {
    return () => modID + ":" + name;
}

export const minecraft = {
    chest: buildNamer("minecraft", "chest"),
};
