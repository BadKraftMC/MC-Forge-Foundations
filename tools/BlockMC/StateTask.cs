using System.Text;


#nullable disable
namespace BlockMC {

    internal abstract class StateTask {
        public string Assets { get; protected init; }
        public string Name { get; protected init; }
        public string Source { get; set; }

        protected void LoadSource(string path) {
            byte[] buffer;

            using(var stream = File.Open(path, FileMode.Open, FileAccess.Read)) {
                buffer = new byte[stream.Length];
                stream.Read(buffer);
            }

            Source = Encoding.UTF8.GetString(buffer);
            Transform();
        }

        protected abstract void Transform( );

        public override string ToString( ) {
            return Source;
        }
    }

    internal class BlockState : StateTask {
        private const string SOURCE = ".\\sources\\blockstates.json";

        private string Mod { get; }
        private Model Model { get; }

        public BlockState(string assets, Project project) {
            Mod = project.mod;
            Model = project.model;
            Name = project.name;
            Assets = assets;

            LoadSource(SOURCE);
        }

        protected override void Transform( ) {
            Source = Source.Replace("%mod%", Mod)
                .Replace("%model%", Model.type)
                .Replace("%name%", Name);
        }
    }

    internal class ModelBlock : StateTask {
        private const string SOURCE = ".\\sources\\modelsblock.json";

        private string Mod { get; }
        private Model Model { get; }

        public ModelBlock(string assets, Project project) {
            Mod = project.mod;
            Model = project.model;
            Name = project.name;
            Assets = assets;

            LoadSource(SOURCE);
        }

        protected override void Transform( ) {
            Source = Source.Replace("%parent%", Model.block_parent)
                .Replace("%mod%", Mod)
                .Replace("%model%", Model.type)
                .Replace("%name%", Name);
        }
    }

    internal class ModelItem : StateTask {
        private const string BLOCK_SOURCE = ".\\sources\\modelsitem_block.json";
        private const string ITEM_SOURCE = ".\\sources\\modelsitem_item.json";

        private string Mod { get; }
        private Model Model { get; }

        public ModelItem(string assets, Project project) {
            Mod = project.mod;
            Model = project.model;
            Name = project.name;
            Assets = assets;

            LoadSource(Model.type == "block" ? BLOCK_SOURCE : ITEM_SOURCE);
        }

        protected override void Transform( ) {
            Source = Source
                .Replace("%mod%", Mod)
                .Replace("%parent%", Model.item_parent)
                .Replace("%name%", Name);
        }
    }
}
