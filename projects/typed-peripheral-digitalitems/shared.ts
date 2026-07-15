/** Timing metadata shared by every digital identifier. */
export declare type IDInfo = {
  /** Current server game time, in ticks, when this information was requested. */
  currentTime: number;
  /** Server game time, in ticks, when the identifier was created. */
  digitizedAt: number;
  /** Server game time at which the identifier expires when decay is enabled. */
  decaysAt: number;
  /** Server game time, in ticks, when the identifier was last refreshed. */
  lastRefresh: number;
};

/** Metadata and current item contents associated with an item identifier. */
export declare type ItemIDInfo = IDInfo & {
  /** Full CC:Tweaked item detail, including the remaining digitized count. */
  item: ItemDetail;
};
