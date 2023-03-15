
#nullable disable
namespace BlockMC {

    internal class Project {
        public string root { get; set; }
        public string mod { get; set; }
        public Model model { get; set; }
        public string name { get; set; }
        public string texture { get; set; }
    }

    internal class Model {
        public string type { get; set; }
        public string item_parent { get; set; }
        public string block_parent { get; set; }
    }
}
